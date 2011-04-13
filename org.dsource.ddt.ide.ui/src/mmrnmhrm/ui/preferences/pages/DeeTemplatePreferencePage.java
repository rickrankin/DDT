package mmrnmhrm.ui.preferences.pages;

import mmrnmhrm.ui.DeePlugin;
import mmrnmhrm.ui.editor.templates.DeeTemplateAccess;
import mmrnmhrm.ui.text.DeePartitions;
import mmrnmhrm.ui.text.DeeSimpleSourceViewerConfiguration;

import org.eclipse.dltk.ui.templates.ScriptTemplateAccess;
import org.eclipse.dltk.ui.templates.ScriptTemplatePreferencePage;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.jface.text.IDocument;

public class DeeTemplatePreferencePage extends ScriptTemplatePreferencePage {
	
	public final static String PAGE_ID = DeePlugin.EXTENSIONS_IDPREFIX + "preferences.editor.CodeTemplates";
	
	
	@Override
	protected void setPreferenceStore() {
		setPreferenceStore(DeePlugin.getDefault().getPreferenceStore());
	}
	
	@Override
	protected ScriptTemplateAccess getTemplateAccess() {
		return DeeTemplateAccess.getInstance();
	}
	
	@Override
	protected ScriptSourceViewerConfiguration createSourceViewerConfiguration() {
		return new DeeSimpleSourceViewerConfiguration(getTextTools().getColorManager(), getPreferenceStore(), null,
				DeePartitions.DEE_PARTITIONING, false);
	}
	
	@Override
	protected void setDocumentParticioner(IDocument document) {
		getTextTools().setupDocumentPartitioner(document, DeePartitions.DEE_PARTITIONING);
	}
	
	protected ScriptTextTools getTextTools() {
		return DeePlugin.getDefault().getTextTools();
	}
	
}
