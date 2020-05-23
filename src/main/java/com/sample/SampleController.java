package com.sample;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class SampleController {
	@RequestMapping(path = "/sample/upload", method = RequestMethod.GET)
	String uploadview(Model model) {
	  return "sample/upload";
	}

	@PostMapping("/sample/express")
	String expressImage(Model model,UploadForm uploadForm) {

		if (uploadForm.getFile().isEmpty()) {
			return"sample/upload";
		}
		model.addAttribute("file", uploadForm.getFile());

        StringBuffer data = new StringBuffer();
        String base64 = null;
        try {
        	 base64 = new String(Base64.encodeBase64(uploadForm.getFile().getBytes()),"ASCII");
		} catch (Exception e) {
			return"sample/upload";
		}
        data.append("data:image/jpeg;base64,");
        data.append(base64);
        model.addAttribute("base64image",data.toString());
        return "sample/express";
	}

	@PostMapping("/sample/uploadComplete")
	String upload(Model model, UploadForm uploadForm) {

		if (uploadForm.getFile() == null) {
	    return "sample/upload";
	  }

	  // check upload distination directory.If there was no directory, make
	  // func.
	  Path path = Paths.get("/Users/村山　光/image");
	  if (!Files.exists(path)) {
	    try {
	      Files.createDirectory(path);
	    } catch (NoSuchFileException ex) {
	      System.err.println(ex);
	    } catch (IOException ex) {
	      System.err.println(ex);
	    }
	  }

	  int dot = uploadForm.getFile().getOriginalFilename().lastIndexOf(".");
	  String extention = "";
	  if (dot > 0) {
		  //ドットより後ろの文字列を取得している（＝拡張子を得ている？）
	    extention = uploadForm.getFile().getOriginalFilename().substring(dot).toLowerCase();
	  }
	  String filename = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now());
	  //ファイルのディレクトリを設定
	  Path uploadfile = Paths
	      .get("/Users/村山　光/image/" + filename + extention);

	  try (OutputStream os = Files.newOutputStream(uploadfile, StandardOpenOption.CREATE)) {
	    byte[] bytes = uploadForm.getFile().getBytes();
	    os.write(bytes);
	  } catch (IOException ex) {
	    System.err.println(ex);
	  }

	  //リダイレクトが必要
	  return "redirect:/sample/upload";
	}
}
