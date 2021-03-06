package cn.wanghaomiao.seimi.struct;
/*
   Copyright 2015 - now original author

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */


import cn.wanghaomiao.seimi.core.SeimiBeanResolver;
import cn.wanghaomiao.xpath.model.JXDocument;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Map;

/**
 * @author 汪浩淼 [et.tw@163.com]
 *         Date: 2015/05/12.
 */
public class Response extends CommonObject {
    private BodyType bodyType;
    private HttpResponse httpResponse;
    private HttpEntity reponseEntity;
    private Request request;
    private String charset;
    private String referer;
    private byte[] data;
    private String content;
    /**
     * 这个主要用于存储上游传递的一些自定义数据
     */
    private Map<String,String> meta;
    private String url;
    private Map<String,String> params;
    /**
     * 网页内容真实源地址
     */
    private String realUrl;

    private Logger logger = LoggerFactory.getLogger(Response.class);

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public BodyType getBodyType() {
        return bodyType;
    }

    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, String> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, String> meta) {
        this.meta = meta;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }

    public void setHttpResponse(HttpResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    public HttpEntity getReponseEntity() {
        return reponseEntity;
    }

    public void setReponseEntity(HttpEntity reponseEntity) {
        this.reponseEntity = reponseEntity;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getRealUrl() {
        return realUrl;
    }

    public void setRealUrl(String realUrl) {
        this.realUrl = realUrl;
    }

    /**
     * 通过bean中定义的Xpath注解进行自动填充
     * @param bean
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T render(Class<T> bean) throws Exception {
        if (bodyType.equals(BodyType.TEXT)){
            return SeimiBeanResolver.parse(bean,this.content);
        }else {
            throw new RuntimeException("can not parse struct from binary");
        }
    }

    public JXDocument document(){
        return BodyType.TEXT.equals(bodyType)&&content!=null?new JXDocument(content):null;
    }

    public void saveTo(File targetFile){
        FileChannel fo = null;
        try {
            File pf = targetFile.getParentFile();
            if (!pf.exists()){
                pf.mkdirs();
            }
            fo = new FileOutputStream(targetFile).getChannel();
            if (BodyType.TEXT.equals(bodyType)){
                fo.write(ByteBuffer.wrap(getContent().getBytes()));
            }else {
                fo.write(ByteBuffer.wrap(getData()));
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            if (fo!=null){
                try {
                    fo.close();
                } catch (IOException ignore) {
                    logger.error(ignore.getMessage(),ignore);
                }
            }
        }
    }
}
