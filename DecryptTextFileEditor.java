package com.jcocktail.utils.file.editors;

import java.io.File;

import org.jasypt.util.text.BasicTextEncryptor;

import com.jcocktail.utils.file.IFileEditor;
import com.jcocktail.utils.file.TextFile;


public class DecryptTextFileEditor implements IFileEditor {

	String fileNameExt = "tencr";
	String key;

	public DecryptTextFileEditor(String key) {
		super();
		this.key = key;
	}

	public DecryptTextFileEditor(String key, String fileNameExt) {
		super();
		this.key = key;
		this.fileNameExt = fileNameExt;
	}

	public String getFileNameExt() {
		return fileNameExt;
	}

	public void setFileNameExt(String fileNameExt) {
		this.fileNameExt = fileNameExt;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public File edit(File file) throws Exception {

		String srcText = new TextFile(file.getAbsolutePath()).getContent(); 
	
		// Decrypt
		BasicTextEncryptor encryptor = new BasicTextEncryptor();
		encryptor.setPassword(key);
		String destText = encryptor.decrypt(srcText);

		String destFilePath = file.getAbsolutePath().substring(0,
				file.getAbsolutePath().lastIndexOf("." + fileNameExt));
		
		TextFile destFile = new TextFile(destFilePath);
		destFile.print(destText, false); 
		
		System.out.println("Decrypted Text File[" + destFile + "]!");
		
		return destFile;
	}

}
