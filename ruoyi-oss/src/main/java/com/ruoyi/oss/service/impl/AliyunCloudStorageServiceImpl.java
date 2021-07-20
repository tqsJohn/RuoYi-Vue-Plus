package com.ruoyi.oss.service.impl;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.ruoyi.oss.entity.UploadResult;
import com.ruoyi.oss.enumd.CloudServiceEnumd;
import com.ruoyi.oss.exception.OssException;
import com.ruoyi.oss.factory.OssFactory;
import com.ruoyi.oss.properties.CloudStorageProperties;
import com.ruoyi.oss.properties.CloudStorageProperties.AliyunProperties;
import com.ruoyi.oss.service.abstractd.AbstractCloudStorageService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * 阿里云存储
 *
 * @author Lion Li
 */
@Lazy
@Service
public class AliyunCloudStorageServiceImpl extends AbstractCloudStorageService implements InitializingBean {

	private final OSSClient client;
	private final AliyunProperties properties;

	@Autowired
	public AliyunCloudStorageServiceImpl(CloudStorageProperties properties) {
		this.properties = properties.getAliyun();
		try {
			ClientConfiguration configuration = new ClientConfiguration();
			DefaultCredentialProvider credentialProvider = new DefaultCredentialProvider(
				this.properties.getAccessKeyId(),
				this.properties.getAccessKeySecret());
			client = new OSSClient(this.properties.getEndpoint(), credentialProvider, configuration);
		} catch (Exception e) {
			throw new IllegalArgumentException("阿里云存储配置错误! 请检查系统配置!");
		}
	}

	@Override
	public String getServiceType() {
		return CloudServiceEnumd.ALIYUN.getValue();
	}

	@Override
	public UploadResult upload(byte[] data, String path, String contentType) {
		return upload(new ByteArrayInputStream(data), path, contentType);
	}

	@Override
	public UploadResult upload(InputStream inputStream, String path, String contentType) {
		try {
			client.putObject(this.properties.getBucketName(), path, inputStream);
		} catch (Exception e) {
			throw new OssException("上传文件失败，请检查配置信息");
		}
		return new UploadResult().setUrl(properties.getEndpoint() + "/" + path).setFilename(path);
	}

	@Override
	public void delete(String path) {
		path = path.replace(this.properties.getEndpoint() + "/", "");
		try {
			client.deleteObject(this.properties.getBucketName(), path);
		} catch (Exception e) {
			throw new OssException("上传文件失败，请检查配置信息");
		}
	}

	@Override
	public UploadResult uploadSuffix(byte[] data, String suffix, String contentType) {
		return upload(data, getPath(this.properties.getPrefix(), suffix), contentType);
	}

	@Override
	public UploadResult uploadSuffix(InputStream inputStream, String suffix, String contentType) {
		return upload(inputStream, getPath(this.properties.getPrefix(), suffix), contentType);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		OssFactory.register(getServiceType(),this);
	}
}