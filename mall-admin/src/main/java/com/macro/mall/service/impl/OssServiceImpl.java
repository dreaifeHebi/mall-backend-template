package com.macro.mall.service.impl;

import com.macro.mall.dto.OssCallbackResult;
import com.macro.mall.dto.OssPolicyResult;
import com.macro.mall.service.OssService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * OSS policy compatibility service.
 *
 * The template core does not depend on a bundled object-storage service. File uploads
 * should use /minio/upload, which is implemented as local disk storage by default.
 * S3/R2/MinIO/Aliyun OSS implementations belong in optional extensions.
 */
@Service
public class OssServiceImpl implements OssService {

	@Value("${mall.upload.url:/uploads}")
	private String uploadUrl;

	@Override
	public OssPolicyResult policy() {
		OssPolicyResult result = new OssPolicyResult();
		result.setAccessKeyId("");
		result.setPolicy("");
		result.setSignature("");
		result.setDir("");
		result.setCallback("");
		result.setHost(uploadUrl);
		return result;
	}

	@Override
	public OssCallbackResult callback(HttpServletRequest request) {
		OssCallbackResult result= new OssCallbackResult();
		String filename = request.getParameter("filename");
		result.setFilename(filename);
		result.setSize(request.getParameter("size"));
		result.setMimeType(request.getParameter("mimeType"));
		result.setWidth(request.getParameter("width"));
		result.setHeight(request.getParameter("height"));
		return result;
	}

}
