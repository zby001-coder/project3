package com.example.mypractice.model.web;

import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.Part;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 可重复读取流或parts的请求，采用缓存的策略完成可重复读
 * 但当前无法完成同时读取part和inputStream的功能
 * part的优先级比inputStream更高，即如果有part，就不保证inputStream能被读取
 * 支持multipart的多文件上传
 *
 * @author 张贝易
 */
public class RepeatableReadingRequest extends HttpServletRequestWrapper {
    /**
     * 请求体最大缓冲区大小，10MB
     */
    private int bufferSize = 1024 * 1024 * 15;
    /**
     * 一个分片大小，提高读取网络输入流的效率
     */
    private int chunkSize = 1024 * 1024;
    /**
     * 重复使用的缓冲区
     */
    private byte[] buffer;
    /**
     * 缓存的part参数，用来防止multiPart形式的请求
     */
    private HashMap<String, Part> partMap = new HashMap<>();
    private ArrayList<Part> parts = new ArrayList<>();

    public RepeatableReadingRequest(HttpServletRequest request) {
        super(request);
        //将请求的part保存起来
        try {
            parts.addAll(request.getParts());
            HashSet<String> partNames = new HashSet<>();
            for (Part part : parts) {
                partNames.add(part.getName());
            }
            for (String partName : partNames) {
                partMap.put(partName, request.getPart(partName));
            }
        } catch (Exception ignored) {
            //异常说明没有part，跳过即可
        }
        //将请求的输入流保存起来
        try {
            //缓冲区读取到的长度
            int len = 0;
            //创建一个缓冲区
            byte[] tempBuffer = new byte[bufferSize];
            ServletInputStream inputStream = request.getInputStream();
            //由于网络的输入流不稳定，所以需要循环读取，否则可能一次只读了一部分
            while (true) {
                int tag = inputStream.read(tempBuffer, len, chunkSize);
                if (tag != -1) {
                    len += tag;
                } else {
                    break;
                }
            }
            if (len < 0) {
                //防止get请求导致的负数长度
                len = 0;
            }
            buffer = Arrays.copyOf(tempBuffer, len);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new RepeatableReadStream(buffer);
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return this.parts;
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {
        return partMap.get(name);
    }

    /**
     * 用spring定义的BodyInputStream，由于是private，所以直接复制过来
     */
    private static class RepeatableReadStream extends ServletInputStream {

        private final InputStream delegate;

        public RepeatableReadStream(byte[] body) {
            this.delegate = new ByteArrayInputStream(body);
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int read() throws IOException {
            return this.delegate.read();
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return this.delegate.read(b, off, len);
        }

        @Override
        public int read(byte[] b) throws IOException {
            return this.delegate.read(b);
        }

        @Override
        public long skip(long n) throws IOException {
            return this.delegate.skip(n);
        }

        @Override
        public int available() throws IOException {
            return this.delegate.available();
        }

        @Override
        public void close() throws IOException {
            this.delegate.close();
        }

        @Override
        public synchronized void mark(int readlimit) {
            this.delegate.mark(readlimit);
        }

        @Override
        public synchronized void reset() throws IOException {
            this.delegate.reset();
        }

        @Override
        public boolean markSupported() {
            return this.delegate.markSupported();
        }
    }
}