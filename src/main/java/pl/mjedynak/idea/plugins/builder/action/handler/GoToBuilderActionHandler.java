package pl.mjedynak.idea.plugins.builder.action.handler;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import pl.mjedynak.idea.plugins.builder.displayer.PopupDisplayer;
import pl.mjedynak.idea.plugins.builder.factory.PopupListFactory;
import pl.mjedynak.idea.plugins.builder.finder.BuilderFinder;
import pl.mjedynak.idea.plugins.builder.helper.PsiHelper;
import pl.mjedynak.idea.plugins.builder.verifier.BuilderVerifier;

import javax.swing.*;

public class GoToBuilderActionHandler extends EditorActionHandler {

    private PsiHelper psiHelper;

    private BuilderVerifier builderVerifier;

    private BuilderFinder builderFinder;

    private PopupDisplayer popupDisplayer;

    private PopupListFactory popupListFactory;

    public GoToBuilderActionHandler(PsiHelper psiHelper, BuilderVerifier builderVerifier, BuilderFinder builderFinder, PopupDisplayer popupDisplayer, PopupListFactory popupListFactory) {
        this.psiHelper = psiHelper;
        this.builderVerifier = builderVerifier;
        this.builderFinder = builderFinder;
        this.popupDisplayer = popupDisplayer;
        this.popupListFactory = popupListFactory;
    }

    @Override
    public void execute(Editor editor, DataContext dataContext) {
        Project project = (Project) dataContext.getData(DataKeys.PROJECT.getName());
        PsiClass psiClassFromEditor = psiHelper.getPsiClassFromEditor(editor, project);
        if (psiClassFromEditor != null) {
            navigateOrDisplay(editor, psiClassFromEditor);
        }
    }

    private void navigateOrDisplay(Editor editor, PsiClass psiClassFromEditor) {
        boolean isBuilder = builderVerifier.isBuilder(psiClassFromEditor);
        PsiClass classToGo = findClassToGo(psiClassFromEditor, isBuilder);
        if (classToGo != null) {
            psiHelper.navigateToClass(classToGo);
        } else if (canDisplayPopup(isBuilder, classToGo)) {
            displayPopup(editor);
        }
    }

    private boolean canDisplayPopup(boolean isBuilder, PsiClass classToGo) {
        return classToGo == null && !isBuilder;
    }

    private void displayPopup(Editor editor) {
        JList popupList = popupListFactory.getPopupList();
        popupDisplayer.displayPopupChooser(editor, popupList, new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    private PsiClass findClassToGo(PsiClass psiClassFromEditor, boolean isBuilder) {
        if (isBuilder) {
            return builderFinder.findClassForBuilder(psiClassFromEditor);
        }
        return builderFinder.findBuilderForClass(psiClassFromEditor);
    }
}