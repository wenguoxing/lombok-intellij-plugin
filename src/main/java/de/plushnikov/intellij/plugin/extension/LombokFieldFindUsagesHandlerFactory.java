package de.plushnikov.intellij.plugin.extension;

import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.find.findUsages.FindUsagesHandlerFactory;
import com.intellij.openapi.project.DumbService;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiUtilCore;
import de.plushnikov.intellij.plugin.psi.LombokLightMethodBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * It should find calls to getters/setters of some filed changed by lombok accessors
 */
public class LombokFieldFindUsagesHandlerFactory extends FindUsagesHandlerFactory {

  public LombokFieldFindUsagesHandlerFactory() {
  }

  @Override
  public boolean canFindUsages(@NotNull PsiElement element) {
    if (element instanceof PsiField && !DumbService.isDumb(element.getProject())) {
      final PsiField psiField = (PsiField) element;
      final PsiClass containingClass = psiField.getContainingClass();
      if (containingClass != null) {
        for (PsiMethod psiMethod : containingClass.getAllMethods()) {
          if(psiMethod instanceof LombokLightMethodBuilder) {
            return true;
          }
        }
      }
    }
    return false;
  }

  @Override
  public FindUsagesHandler createFindUsagesHandler(@NotNull PsiElement element, boolean forHighlightUsages) {
    return new FindUsagesHandler(element) {
      @NotNull
      @Override
      public PsiElement[] getSecondaryElements() {
        final PsiField psiField = (PsiField) getPsiElement();
        final PsiClass containingClass = psiField.getContainingClass();
        if (containingClass != null) {

          final Collection<PsiElement> elements = new ArrayList<PsiElement>();
          processClassMethods(containingClass, psiField, elements);

          for (PsiClass psiClass : containingClass.getInnerClasses()) {
            processClassMethods(psiClass, psiField, elements);
          }

          return PsiUtilCore.toPsiElementArray(elements);
        }
        return PsiElement.EMPTY_ARRAY;
      }

      private void processClassMethods(PsiClass containingClass, PsiField refPsiField, Collection<PsiElement> collector) {
        for (PsiMethod psiMethod : containingClass.getMethods()) {
          if(psiMethod instanceof LombokLightMethodBuilder && psiMethod.getNavigationElement() == refPsiField) {
            collector.add(psiMethod);
          }
        }
      }
    };
  }
}
