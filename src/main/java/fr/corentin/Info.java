package fr.corentin.dto;

import java.util.Objects;

public class Info {
    private String sourceMac = "";
    private String sourceIp = "";
    private String sourcePort = "";

    private String destinationMac = "";
    private String destinationIp = "";
    private String destinationPort = "";

    private String type;
    private String protocol;
    private String flags;


    private String sequenceNumber;
    private String acknowledgmentNumber;
    private String window;
    private String httpContent;

    public String getSourceMac() { return this.sourceMac; }

    public String getSourceIp() { return this.sourceIp; }

    public String getSourcePort() { return this.sourcePort; }

    public String getDestinationMac() { return this.destinationMac; }

    public String getDestinationIp() { return this.destinationIp; }

    public String getDestinationPort() { return this.destinationPort; }

    public String getType() { return this.type; }

    public String getProtocol() { return this.protocol; }

    public String getFlags() { return this.flags; }

    public String getSequenceNumber() { return this.sequenceNumber; }

    public String getAcknowledgmentNumber() { return this.acknowledgmentNumber; }

    public String getWindow() { return this.window; }

    public String getHttpContent() { return this.httpContent; }

    public void setSourceMac(String sourceMac) {
        this.sourceMac = sourceMac;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public void setSourcePort(String sourcePort) {
        this.sourcePort = sourcePort;
    }

    public void setDestinationMac(String destinationMac) {
        this.destinationMac = destinationMac;
    }

    public void setDestinationIp(String destinationIp) {
        this.destinationIp = destinationIp;
    }

    public void setDestinationPort(String destinationPort) {
        this.destinationPort = destinationPort;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setProtocol(String protocol) { this.protocol = protocol; }

    public void setFlags(String flags) {
        this.flags = flags;
    }

    public void setSequenceNumber(String sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public void setAcknowledgmentNumber(String acknowledgmentNumber) { this.acknowledgmentNumber = acknowledgmentNumber; }

    public void setWindow(String window) {
        this.window = window;
    }

    public void setHttpContent(String httpContent) {
        this.httpContent = httpContent;
    }

    @Override
    public String toString() {
        if (Objects.isNull(this.httpContent))
            return new StringBuilder(this.sourcePort).append(" => ").append(this.destinationPort)
                    .append(" [").append(this.flags).append("] ")
                    .append("AN: ").append(this.acknowledgmentNumber)
                    .append("; SN: ").append(this.sequenceNumber)
                    .append(" Window=").append(this.window).toString();
        else return new StringBuilder(" (").append(this.httpContent).append(") ").toString();
    }
}
