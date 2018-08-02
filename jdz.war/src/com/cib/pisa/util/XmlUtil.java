package com.cib.pisa.util;

import com.cfca.util.pki.PKIException;
import com.cib.pisa.bean.BaseIndexHead;
import com.cib.pisa.bean.DataBean;
import com.cib.pisa.bean.FlowDataBean;
import com.cib.pisa.bean.FlowIndexHead;
import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import cn.com.infosec.netsign.agent.*;
import cn.com.infosec.netsign.exceptions.*;
import cn.com.infosec.netsign.resources.Resource;

public class XmlUtil {
    private static int ZBWD_LENGTH = 13;
    private static BigDecimal BD_10THOUSAND = new BigDecimal("10000");

    public void createXML(BaseIndexHead header, List dataList, String subNo) {
        SignEntry signEntry = null;
        PBCAgent2G pbc = null;

        if ("SM2".equals(ResourceUtil.getProperty("netSignKeyType"))) {
            pbc = new PBCAgent2G(false);
            
            pbc.openSignServer(ResourceUtil.getProperty("netSignServerIP"), 
                               ResourceUtil.getProperty("netSignServerPort"), 
                               ResourceUtil.getProperty("netSignServerPassword"));
        } else {
            try {
                signEntry = new SignEntry(ResourceUtil.getProperty("certificate_path"),
                        ResourceUtil.getProperty("password"));
            } catch (PKIException var17) {
                var17.printStackTrace();
                return;
            }
        }

        StringBuilder content = new StringBuilder();
        Document document = DocumentHelper.createDocument();
        Element rootElement = document.addElement("Document");
        this.addElement(rootElement, "SubCode", header.getSubCode());
        content.append(header.getSubCode() + "|");
        this.addElement(rootElement, "Freq", header.getFreq());
        content.append(header.getFreq() + "|");
        this.addElement(rootElement, "SubNo", header.getSubNo());
        content.append(header.getSubNo() + "|");
        this.addElement(rootElement, "MT", "1");
        content.append("1|");
        this.addElement(rootElement, "BankCode", header.getBankCode());
        content.append(header.getBankCode() + "|");
        this.addElement(rootElement, "AT", header.getAt());
        content.append(header.getAt() + "|");
        this.addElement(rootElement, "AC", header.getAc());
        content.append(header.getAc() + "|");
        this.addElement(rootElement, "NbOfTxs", header.getNbOfTxs());
        content.append(header.getNbOfTxs() + "|");
        Element rwElement = null;
        String temp = null;

        DataBean bean;
        for (Iterator iterator = dataList.iterator(); iterator
                .hasNext(); temp = bean.getIndex().substring(0, ZBWD_LENGTH)) {
            bean = (DataBean) iterator.next();
            if ("PROV".equals(header.getAt())) {
                System.out.println(bean.getIndex() + "|" + bean.getValue() + "|" + bean.isNap());
            }

            boolean isNap = bean.isNap();
            String index = bean.getIndex();
            String[] items = index.split("\\.");
            if (bean.getIndex().substring(0, ZBWD_LENGTH).equals(temp)) {
                if (isNap) {
                    this.addElement(rwElement, "VV2", "NAP");
                    content.append("NAP|");
                } else {
                    this.addElement(rwElement, "VV2", formatData(bean.getValue(), bean.getUnit()));
                    content.append(formatData(bean.getValue(), bean.getUnit()) + "|");
                }
            } else {
                rwElement = this.addElement(rootElement, "RW", (String) null);
                this.addElement(rwElement, "IN", items[0]);
                content.append(items[0] + "|");
                this.addElement(rwElement, "DN", items[1]);
                content.append(items[1] + "|");
                if (isNap) {
                    this.addElement(rwElement, "VV", "NAP");
                    content.append("NAP|");
                } else {
                    this.addElement(rwElement, "VV", formatData(bean.getValue(), bean.getUnit()));
                    content.append(formatData(bean.getValue(), bean.getUnit()) + "|");
                }
            }
        }

        try {
            System.out.println(content.toString());
            if ("SM2".equals(ResourceUtil.getProperty("netSignKeyType"))) {
                this.addElement(rootElement, "PtcptSgntr", pbc.dettachedSign(content.toString().getBytes(), ResourceUtil.getProperty("netSignCertDN")));
            } else {
                this.addElement(rootElement, "PtcptSgntr", signEntry.sign(content.toString()));
            }
        } catch (Exception var16) {
            var16.printStackTrace();
        }

        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");
            format.setIndent(true);
            format.setIndent("\t");
            format.setNewlines(true);
            format.setTrimText(false);
            format.setPadText(true);
            StringBuilder filename = new StringBuilder(
                    ResourceUtil.getProperty("file_path") + File.separator + subNo + File.separator);
            filename.append(header.getSubCode()).append("_").append(header.getSubNo()).append("_")
                    .append(header.getAt()).append("_").append(header.getAc()).append("_").append(header.getSubCode())
                    .append(".xml");
            File file = new File(filename.toString());
            this.mkDir(file.getParentFile());
            if (!file.exists()) {
                file.createNewFile();
            }

            XMLWriter writer = new XMLWriter(new FileWriter(file), format);
            writer.write(document);
            writer.close();
        } catch (Exception var15) {
            var15.printStackTrace();
        }

    }

    public void createFlowXML(FlowIndexHead header, List dataList, String type) {
        SignEntry signEntry = null;
        PBCAgent2G pbc = null;
        
        if ("SM2".equals(ResourceUtil.getProperty("netSignKeyType"))) {
            pbc = new PBCAgent2G(false);
            
            pbc.openSignServer(ResourceUtil.getProperty("netSignServerIP"), 
                               ResourceUtil.getProperty("netSignServerPort"), 
                               ResourceUtil.getProperty("netSignServerPassword"));
        } else {
            try {
                signEntry = new SignEntry(ResourceUtil.getProperty("certificate_path"),
                        ResourceUtil.getProperty("password"));
            } catch (PKIException var15) {
                var15.printStackTrace();
                return;
            }
        }

        StringBuilder content = new StringBuilder();
        Document document = DocumentHelper.createDocument();
        Element rootElement = document.addElement("Document");
        this.addElement(rootElement, "SubCode", header.getSubCode());
        content.append(header.getSubCode() + "|");
        this.addElement(rootElement, "Freq", header.getFreq());
        content.append(header.getFreq() + "|");
        this.addElement(rootElement, "SubNo", header.getSubNo());
        content.append(header.getSubNo() + "|");
        this.addElement(rootElement, "MT", "2");
        content.append("2|");
        this.addElement(rootElement, "IN", header.getIn());
        content.append(header.getIn() + "|");
        this.addElement(rootElement, "DN", header.getDn());
        content.append(header.getDn() + "|");
        this.addElement(rootElement, "NbOfTxs", header.getNbOfTxs());
        content.append(header.getNbOfTxs() + "|");
        Element rwElement = null;
        Iterator iterator = dataList.iterator();

        while (iterator.hasNext()) {
            FlowDataBean bean = (FlowDataBean) iterator.next();
            rwElement = this.addElement(rootElement, "RW", (String) null);
            this.addElement(rwElement, type + "Out", bean.getLc());
            content.append(bean.getLc() + "|");
            this.addElement(rwElement, type + "In", bean.getLr());
            content.append(bean.getLr() + "|");
            this.addElement(rwElement, "VV", bean.getBs().toString());
            content.append(bean.getBs().toString() + "|");
            this.addElement(rwElement, "VV2", bean.getJe().toString());
            content.append(bean.getJe().toString() + "|");
        }

        try {
            if ("SM2".equals(ResourceUtil.getProperty("netSignKeyType"))) {
                this.addElement(rootElement, "PtcptSgntr", pbc.dettachedSign(content.toString().getBytes(), ResourceUtil.getProperty("netSignCertDN")));
            } else {
                this.addElement(rootElement, "PtcptSgntr", signEntry.sign(content.toString()));
            }
        } catch (Exception var14) {
            var14.printStackTrace();
        }

        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");
            format.setIndent(true);
            format.setIndent("\t");
            format.setNewlines(true);
            format.setTrimText(false);
            format.setPadText(true);
            StringBuilder filename = new StringBuilder(
                    ResourceUtil.getProperty("file_path") + File.separator + header.getSubNo() + File.separator);
            filename.append(header.getSubCode()).append("_").append(header.getSubNo()).append("_")
                    .append(header.getIn()).append("_").append(header.getDn()).append("_").append(type).append(".xml");
            File file = new File(filename.toString());
            this.mkDir(file.getParentFile());
            if (!file.exists()) {
                file.createNewFile();
            }

            XMLWriter writer = new XMLWriter(new FileWriter(file), format);
            writer.write(document);
            writer.close();
        } catch (Exception var13) {
            var13.printStackTrace();
        }

    }

    private Element addElement(Element element, String name, String value) {
        Element subElement = element.addElement(name);
        if (value != null) {
            subElement.setText(value);
        }

        return subElement;
    }

    public static String formatData(String s, String unit) {
        if (s != null && !"NAP".equals(s)) {
            BigDecimal b = new BigDecimal(s);
            DecimalFormat formatter;
            if ("INTEGER".equals(unit)) {
                formatter = new DecimalFormat("#0");
                return formatter.format(b);
            } else if ("PERCENT".equals(unit)) {
                formatter = new DecimalFormat("#0.00");
                return formatter.format(b);
            } else {
                return "10THOUSANDS".equals(unit) ? b.divide(BD_10THOUSAND).toString() : b.toString();
            }
        } else {
            return "NAP";
        }
    }

    public static String formatData(BigDecimal b, String unit) {
        if (b == null) {
            return "NAP";
        } else {
            DecimalFormat formatter;
            if ("INTEGER".equals(unit)) {
                formatter = new DecimalFormat("#0");
                return formatter.format(b);
            } else if ("PERCENT".equals(unit)) {
                formatter = new DecimalFormat("#0.00");
                return formatter.format(b);
            } else {
                return "10THOUSANDS".equals(unit) ? b.divide(BD_10THOUSAND).toString() : b.toString();
            }
        }
    }

    public void mkDir(File file) throws Exception {
        if (file.getParentFile().exists()) {
            file.mkdir();
        } else {
            this.mkDir(file.getParentFile());
            file.mkdir();
        }

    }

    public static void main(String[] args) throws Exception {
        
        PBCAgent2G pbc = new PBCAgent2G(false);
        
        pbc.openSignServer(ResourceUtil.getProperty("netSignServerIP"), 
                        ResourceUtil.getProperty("netSignServerPort"), 
                        ResourceUtil.getProperty("netSignServerPassword"));
//                           ResourceUtil.getProperty("netSignServerPort"), 
//                           ResourceUtil.getProperty("netSignServerPassword"));
        
        String sign = pbc.dettachedSign("123456".getBytes(), ResourceUtil.getProperty("netSignCertDN"));
        
        System.out.println(sign);
    }
}