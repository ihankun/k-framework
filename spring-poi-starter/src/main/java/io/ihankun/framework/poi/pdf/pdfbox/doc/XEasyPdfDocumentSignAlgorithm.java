package io.ihankun.framework.poi.pdf.pdfbox.doc;

/**
 * pdf文档签名算法
 *
 * @author hankun
 */
public enum XEasyPdfDocumentSignAlgorithm {
    /**
     * RSA
     */
    NONEwithRSA("RSA", "NONEwithRSA"),
    MD2withRSA("RSA", "MD2withRSA"),
    MD5withRSA("RSA", "MD5withRSA"),
    SHA1withRSA("RSA", "SHA1withRSA"),
    SHA256withRSA("RSA", "SHA256withRSA"),
    SHA384withRSA("RSA", "SHA384withRSA"),
    SHA512withRSA("RSA", "SHA512withRSA"),

    /**
     * DSA
     */
    NONEwithDSA("DSA", "NONEwithDSA"),
    SHA1withDSA("DSA", "SHA1withDSA"),

    /**
     * ECDSA
     */
    NONEwithECDSA("ECDSA", "NONEwithECDSA"),
    SHA1withECDSA("ECDSA", "SHA1withECDSA"),
    SHA256withECDSA("ECDSA", "SHA256withECDSA"),
    SHA384withECDSA("ECDSA", "SHA384withECDSA"),
    SHA512withECDSA("ECDSA", "SHA512withECDSA");

    /**
     * 类型
     */
    private String type;
    /**
     * 名称
     */
    private String name;

    /**
     * 有参构造
     *
     * @param type 类型
     * @param name 名称
     */
    XEasyPdfDocumentSignAlgorithm(String type, String name) {
        this.type = type;
        this.name = name;
    }
}
