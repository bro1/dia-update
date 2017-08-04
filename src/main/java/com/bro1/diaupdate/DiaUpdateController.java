package com.bro1.diaupdate;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.ElementFilter;
import org.jdom2.filter.Filter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.jdom2.util.IteratorIterable;

import com.google.common.io.Files;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class DiaUpdateController implements Initializable {

	private static final String UML_CLASS = "UML - Class";

	Document jdomDocument;
	String currentFileName = null;

	public Stage myStage;

	@FXML
	private ListView<String> listClasses;

	@FXML
	private ListView<String> listFields;

	@FXML
	private TextArea newAttributes;

	@FXML
	private Button btnSave;

	@FXML
	private Button load;

	@FXML
	private void onMenuExit(ActionEvent e) {
		Platform.exit();
	}


	@FXML
	void onLoad(ActionEvent event) {
		open();
	}

	private void open() {
		 FileChooser fileChooser = new FileChooser();
		 fileChooser.setTitle("Open Resource File");
		 fileChooser.getExtensionFilters().addAll(
		         new FileChooser.ExtensionFilter("Dia", "*.dia"),
		         new FileChooser.ExtensionFilter("All Files", "*.*"));
		 File selectedFile = fileChooser.showOpenDialog(myStage);
		 if (selectedFile != null) {

			 currentFileName = selectedFile.getAbsolutePath() ;
			 loadDiaFile(currentFileName);
			
		 }
		 
		 
		
	}

	public void loadDiaFile(String gzipFile) {

		ObservableList<String> items = FXCollections.observableArrayList();
		listClasses.setItems(items);

		try {

			GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(gzipFile));

			// the SAXBuilder is the easiest way to create the JDOM2 objects.
			SAXBuilder jdomBuilder = new SAXBuilder();

			// jdomDocument is the JDOM2 Object
			jdomDocument = jdomBuilder.build(gzis);

			System.out.println(jdomDocument.getRootElement().getName());
			ElementFilter ef = new ElementFilter("object");
			IteratorIterable<Element> fl = jdomDocument.getRootElement().getDescendants(ef);
			while (fl.hasNext()) {
				Element el = fl.next();

				// UML - Class
				// Standard - Line
				// UML - Note
				String type = el.getAttributeValue("type");

				if (UML_CLASS.equals(type)) {

					List<Element> fl2 = el.getChildren("attribute", el.getNamespace());

					for (Element eee : fl2) {

						if (eee.getAttributeValue("name").equals("name")) {
							Element se = eee.getChild("string", eee.getNamespace());
							System.out.println(se.getTextNormalize());
							items.add(se.getTextNormalize());
						}
					}

				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {

		listClasses.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> ov, String old_val, String new_val) {

				listFields(new_val);

				// label.setText(new_val);
				// label.setTextFill(Color.web(new_val));
			}

		});

		myStage.setMinWidth(570);
		myStage.setMinHeight(160);
	}

	private void listFields(String new_val) {

		System.out.println(new_val);

		Element cle = getClass(new_val);
		addClassFieldsToList(cle);

	}

	private void addClassFieldsToList(Element cle) {

		Element attributesEl = getAttributesAttribute(cle);
		addAttributestoList(attributesEl);

	}

	private void addAttributestoList(Element attributesEl) {

		ObservableList<String> items = FXCollections.observableArrayList();
		listFields.setItems(items);

		Namespace namespace = attributesEl.getNamespace();
		List<Element> el = attributesEl.getChildren("composite", namespace);

		for (Element e : el) {
			List<Element> nl = e.getChildren("attribute", namespace);
			for (Element e1 : nl) {
				if ("name".equals(e1.getAttributeValue("name"))) {
					String fieldNameAsIs = e1.getChild("string", namespace).getTextNormalize();
					String fieldName = getFieldName(fieldNameAsIs);
					items.add(fieldName);
				}
			}
		}

	}

	private String getFieldName(String fieldNameAsIs) {

		if (fieldNameAsIs.startsWith("#") && fieldNameAsIs.endsWith("#")) {
			return fieldNameAsIs.substring(1, fieldNameAsIs.length() - 1).trim();
		}
		return fieldNameAsIs;

	}

	private Element getAttributesAttribute(Element cle) {

		List<Element> el = cle.getChildren("attribute", cle.getNamespace());
		for (Element e : el) {
			if (e.getAttributeValue("name").equals("attributes")) {
				return e;
			}
		}

		return null;

	}

	private Element getClass(String className) {

		ElementFilter ef = new ElementFilter("object");
		IteratorIterable<Element> fl = jdomDocument.getRootElement().getDescendants(ef);
		while (fl.hasNext()) {
			Element el = fl.next();

			// UML - Class
			// Standard - Line
			// UML - Note
			String type = el.getAttributeValue("type");

			if (UML_CLASS.equals(type)) {

				List<Element> fl2 = el.getChildren("attribute", el.getNamespace());

				for (Element eee : fl2) {

					if (eee.getAttributeValue("name").equals("name")) {
						Element se = eee.getChild("string", eee.getNamespace());
						String currentclname = se.getTextNormalize();

						if (className.equals(currentclname)) {
							return el;
						}
					}
				}

			}

		}

		return null;

	}

	String xmltempl = "<dia:diagram xmlns:dia=\"http://www.lysator.liu.se/~alla/dia/\">\n"
			+ "<dia:composite type=\"umlattribute\">\n" + " <dia:attribute name=\"name\">\n"
			+ " <dia:string>#template#</dia:string>\n" + " </dia:attribute>\n" + " <dia:attribute name=\"type\">\n"
			+ " <dia:string>##</dia:string>\n" + " </dia:attribute>\n" + " <dia:attribute name=\"value\">\n"
			+ " <dia:string>##</dia:string>\n" + " </dia:attribute>\n" + " <dia:attribute name=\"comment\">\n"
			+ " <dia:string>##</dia:string>\n" + " </dia:attribute>\n" + " <dia:attribute name=\"visibility\">\n"
			+ " <dia:enum val=\"0\"/>\n" + " </dia:attribute>\n" + " <dia:attribute name=\"abstract\">\n"
			+ " <dia:boolean val=\"false\"/>\n" + " </dia:attribute>\n" + " <dia:attribute name=\"class_scope\">\n"
			+ " <dia:boolean val=\"false\"/>\n" + " </dia:attribute>\n" + " </dia:composite>\n" + "</dia:diagram>";

	@FXML
	void onAddFields(ActionEvent event) {

		SAXBuilder sb = new SAXBuilder();
		Document d = null;
		String selectedClassName = listClasses.getSelectionModel().getSelectedItem();

		try {
			d = sb.build(new StringReader(xmltempl));
		} catch (Exception e) {
			e.printStackTrace();
		}

		Namespace ns = d.getRootElement().getNamespace();
		Element el = d.getRootElement().getChild("composite", ns);

		String fieldss = newAttributes.getText();

		String[] fl = fieldss.split("\n");
		for (String s : fl) {

			if (!s.trim().isEmpty()) {

				s = s.trim();

				Element clonee = el.clone();
				List<Element> cel = clonee.getChildren("attribute", ns);
				for (Element ee : cel) {
					if ("name".equals(ee.getAttributeValue("name"))) {
						ee.getChild("string", ns).setText("#" + s + "#");
					}
				}

				
				Element classel = getClass(selectedClassName);
				Element attribuesel = getAttributesAttribute(classel);
				attribuesel.addContent(clonee);
			}

		}
		
		
		newAttributes.clear();
		listFields(selectedClassName);

	}

	@FXML
	void onSave(ActionEvent event) {
		
		if (currentFileName == null) return;
		
		if (!backupFile()) {
			System.out.println("Backup could not be created. File not overwritten");
			return;
		}
		
		XMLOutputter xo = new XMLOutputter();

		try {
			GZIPOutputStream zip = new GZIPOutputStream(new FileOutputStream(currentFileName));
			xo.output(jdomDocument, zip);
			zip.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean backupFile() {
		for (int i = 1; i < 10000; i++) {
			File bf = new File(currentFileName + "~" + i);
			if (!bf.exists()) {
				try {
					Files.copy(new File(currentFileName), bf);
					return true;
				} catch (Exception e) {
					e.printStackTrace(System.err);
					
					return false;
				}
			}
		}
		
		return false;
	}

}
