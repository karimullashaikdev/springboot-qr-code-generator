package com.karim.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.karim.util.QRCodeUtil;

@RestController
public class QRCodeController {

	@GetMapping(value = "/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
	public byte[] generateQRCode(@RequestParam String text) throws Exception {

		return QRCodeUtil.generateQRCode(text, 300, 300);
	}
}
