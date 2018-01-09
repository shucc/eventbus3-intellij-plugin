package com.kgmyshin.ideaplugin.eventbus3;

import com.intellij.psi.*;
import com.intellij.usages.Usage;
import com.intellij.usages.UsageInfo2UsageAdapter;

/**
 * Created by kgmyshin on 2015/06/07.
 */
public class ReceiverFilter implements Filter {
    @Override
    public boolean shouldShow(Usage usage) {
        PsiElement element = ((UsageInfo2UsageAdapter) usage).getElement();
        if (!(element instanceof PsiJavaCodeReferenceElement)) {
            return false;
        }
        if (!((element = element.getParent()) instanceof PsiTypeElement)) {
            return false;
        }
        if (!((element = element.getParent()) instanceof PsiParameter)) {
            return false;
        }
        if (!((element = element.getParent()) instanceof PsiParameterList)) {
            return false;
        }
        if ((element = element.getParent()) instanceof PsiMethod) {
            PsiMethod method = (PsiMethod) element;
            if (PsiUtils.isEventBusReceiver(method)) {
                return true;
            }
        }
        return false;
    }
}
