//package io.ihankun.framework.gateway.entity;
//
//import io.ihankun.framework.core.base.BaseEntity;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import org.springframework.cloud.gateway.route.Route;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//
//import java.util.Map;
//
//@AllArgsConstructor
//@Data
//public class GatewayParam extends BaseEntity {
//
//    /**
//     * 请求完整地址
//     */
//    private String url;
//
//    private Route route;
//
//    /**
//     * 请求方式 GET POST
//     */
//    private HttpMethod httpMethod;
//
//    /**
//     * 参数类型 application/json
//     */
//    private String contentType;
//
//    /**
//     * RequestParam参数
//     */
//    private Map<String, String> params;
//
//    /**
//     * RequestBody参数
//     */
//    private String body;
//
//    /**
//     * 原有Headers
//     */
//    private HttpHeaders headers;
//
//
//    @Override
//    public String toString() {
//        return "GatewayParam{" +
//                "url='" + url + '\'' +
//                ", httpMethod=" + httpMethod +
//                ", contentType='" + contentType + '\'' +
//                ", params=" + params +
//                ", body='" + body + '\'' +
//                ", headers=" + headers +
//                '}';
//    }
//}
