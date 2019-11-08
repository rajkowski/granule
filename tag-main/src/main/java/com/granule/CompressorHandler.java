/*
 * Copyright 2010 Granule Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.granule;

import com.granule.cache.TagCacheFactory;
import com.granule.utils.HttpHeaders;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.digest.DigestUtils;

/*
 * User: Dario Wunsch Date: 20.08.2010 Time: 4:20:09
 */
/**
 * @author Dario Wunsch
 * @author Looney32768
 * @author John Yeary <jyeary@bluelotussoftware.com>
 */
public class CompressorHandler {

    public void handle(HttpServletRequest request, HttpServletResponse response, String id) throws IOException, ServletException {
        if (id != null) {
            CompressorSettings settings = TagCacheFactory.getCompressorSettings(request.getServletContext().getRealPath("/"));

            CachedBundle bundle;
            try {
                bundle = TagCacheFactory.getInstance().getCompiledBundle(new RealRequestProxy(request), settings, id);
            } catch (JSCompileException e) {
                throw new ServletException(e);
            }

            if (bundle == null) {
                response.setStatus(404);
                return;
            }

            // Check for a last-modified header and return 304 if possible
            long lastModified = bundle.getModifyDate();
            if (lastModified > 0) {
                long headerValue = request.getDateHeader("If-Modified-Since");
                if (lastModified <= headerValue + 1000) {
                    response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                    return;
                }
            }
            response.setHeader("Content-Type", bundle.getMimeType() + "; charset=utf-8");
            if (lastModified > 0) {
                response.setDateHeader("Last-Modified", lastModified);
            } else {
                HttpHeaders.setCacheExpireDate(response, 6048000);
            }
            response.setHeader("ETag", DigestUtils.md5Hex(bundle.getBundleValue()));

            OutputStream os = response.getOutputStream();
            try {
                if (settings.isGzipOutput() && gzipSupported(request)) {
                    response.setHeader("Content-Encoding", "gzip");
                    os.write(bundle.getBundleValue());
                } else {
                    bundle.getUncompressedScript(os);
                }
                os.flush();
            } finally {
                os.close();
            }
        }
    }

    private boolean gzipSupported(HttpServletRequest request) {
        String acceptEncoding = request.getHeader("Accept-Encoding");
        if (acceptEncoding == null || !acceptEncoding.contains("gzip")) {
            return false;
        }
        String userAgent = request.getHeader("user-agent");
        if (userAgent == null) {
            return false;
        }
        if (userAgent.contains("MSIE 6") && !userAgent.contains("SV1")) {
            return false;
        }
        return true;
    }
}
