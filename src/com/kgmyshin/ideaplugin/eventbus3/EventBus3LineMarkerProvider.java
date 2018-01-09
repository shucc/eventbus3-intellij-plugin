package com.kgmyshin.ideaplugin.eventbus3;

import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtilBase;
import com.intellij.ui.awt.RelativePoint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;
import java.util.List;

/**
 * Created by kgmyshin on 15/06/08.
 */
public class EventBus3LineMarkerProvider implements LineMarkerProvider {

    public static final Icon ICON = IconLoader.getIcon("/icons/icon.png");

    public static final int MAX_USAGES = 100;

    private static GutterIconNavigationHandler<PsiElement> SHOW_SENDERS = (mouseEvent, psiElement) -> {
        if (psiElement instanceof PsiMethod) {
            Project project = psiElement.getProject();
            JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
            PsiClass eventBusClass = javaPsiFacade.findClass("org.greenrobot.eventbus.EventBus", GlobalSearchScope.allScope(project));
            PsiMethod postMethod = eventBusClass.findMethodsByName("post", false)[0];
            PsiMethod method = (PsiMethod) psiElement;
            PsiClass eventClass = ((PsiClassType) method.getParameterList().getParameters()[0].getTypeElement().getType()).resolve();
            new ShowUsagesAction(new SenderFilter(eventClass)).startFindUsages(postMethod, new RelativePoint(mouseEvent), PsiUtilBase.findEditor(psiElement), MAX_USAGES);
        }
    };

    private static GutterIconNavigationHandler<PsiElement> SHOW_RECEIVERS = (mouseEvent, psiElement) -> {
        if (psiElement instanceof PsiMethodCallExpression) {
            PsiMethodCallExpression expression = (PsiMethodCallExpression) psiElement;
            PsiType[] expressionTypes = expression.getArgumentList().getExpressionTypes();
            if (expressionTypes.length > 0) {
                PsiClass eventClass = PsiUtils.getClass(expressionTypes[0]);
                if (eventClass != null) {
                    new ShowUsagesAction(new ReceiverFilter()).startFindUsages(eventClass, new RelativePoint(mouseEvent), PsiUtilBase.findEditor(psiElement), MAX_USAGES);
                }
            }
        }
    };

    @Nullable
    @Override
    public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement psiElement) {
        if (PsiUtils.isEventBusPost(psiElement)) {
            return new LineMarkerInfo<>(psiElement, psiElement.getTextRange(), ICON,
                    Pass.UPDATE_ALL, null, SHOW_RECEIVERS,
                    GutterIconRenderer.Alignment.LEFT);
        } else if (PsiUtils.isEventBusReceiver(psiElement)) {
            return new LineMarkerInfo<>(psiElement, psiElement.getTextRange(), ICON,
                    Pass.UPDATE_ALL, null, SHOW_SENDERS,
                    GutterIconRenderer.Alignment.LEFT);
        }
        return null;
    }


    @Override
    public void collectSlowLineMarkers(@NotNull List<PsiElement> list, @NotNull Collection<LineMarkerInfo> collection) {
    }
}
