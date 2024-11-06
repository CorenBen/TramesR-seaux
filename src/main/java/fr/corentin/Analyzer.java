package fr.corentin.analyser;

import fr.corentin.dto.Info;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Analyzer {
    private List<String> trameList; // Contient la liste des trames
    private final List<Info> infoList = new ArrayList<>();
    private String rendu = ""; // Son contenu sera ecrit dans un fichier texte
    private String type = "";
    private String protocol = "";

    private int port_source;
    private int port_dest;
    private int port_prec = 0;
    private int opIP = 0; //Taille des options IPv4 redefinis dans tailleOptionsIP()
    private int opTCP = 0;//Taille des options TCP redefinis dans tailleOptionsTCP()
    private int data = 0; //Taille du paquet data servant � d�finir SN et AN
    private int debut_padding = 0; //Indique le premier octet du padding

    private int SN = 0; //Par rapport au client
    private int AN = 0; //Par rapport au serveur

    public Analyzer(List<String> t) {
        this.trameList = t;
        ecrire();
    }

    public void ecrire() {
        try {
            FileWriter fw = new FileWriter("InfosTrace.txt");
            decrypt(fw);
            fw.write(rendu);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void decrypt(FileWriter f) {
        String s; //Contient la trame n
        String[] s2; //Contient un tableau d'octet de la trame n
        for (int i = 0; i < trameList.size(); i++) {
            Info inf = new Info();
            rendu += "Trame " + (i + 1) + " : \n";
            s = trameList.get(i);
            s2 = s.split(" ");
            //Couche Ethernet
            dest_sourceMAC(s2, inf);
            type(s2, inf);
            if (type.equals("IPv4")) {
                //Couche IPv4
                dest_sourceIP(s2, inf);
                protocol(s2, inf);
                tailleOptionsIP(s2, inf);
                if (protocol.equals("TCP")) {
                    //Couche TCP
                    dest_sourcePort(s2, inf);
                    flags(s2, inf);
                    seq_ackNumber(s2, i, inf);
                    fenetre(s2, inf);
                    tailleOptionsTCP(s2, inf);
                    padding(s2, inf);
                    if(s2.length>57 + opIP + opTCP) {
                        System.out.println(port_source+" "+port_dest+"\n");
                        if (httpVerif(s2)) {
                            //Couche HTTP
                            httpSource_Dest(s2, inf);
                        }
                    }
                }
            }
            this.infoList.add(inf);
        }
    }

    public int convertHexToInt(String hex) {
        return Integer.valueOf(hex, 16);
    }

    public long convertHexToLong(String hex) {
        return Long.parseLong(hex, 16);
    }

    public String convertHextoString(String hex){
        String str = "";
        for(int i=0;i<hex.length();i+=2)
        {
            String s = hex.substring(i, (i + 2));
            int decimal = Integer.parseInt(s, 16);
            str = str + (char) decimal;
        }
        return str;
    }

    public static String removeLeadingZeros(String str) { return str.replaceFirst("^0+(?!$)", ""); }

    public void dest_sourceMAC(String[] s, Info inf) {
        String s1 = s[0] + ":" + s[1] + ":" + s[2] + ":" + s[3] + ":" + s[4] + ":" + s[5];
        String s2 = s[6] + ":" + s[7] + ":" + s[8] + ":" + s[9] + ":" + s[10] + ":" + s[11];
        rendu += "\t MAC_Dest : " + s1 + "\n";
        rendu += "\t MAC_Source : " + s2 + "\n";
        inf.setDestinationMac(s1);
        inf.setSourceMac(s2);
    }

    public void type(String[] s, Info inf) {
        String s1 = s[12] + s[13];
        if (s1.equals("0806")) {
            type = "ARP";
        }
        if (s1.equals("0800")) {
            type = "IPv4";
        }
        if (s1.equals("86DD")) {
            type = "IPv6";
        }
        rendu += "\t Type : " + type + "\n";
        inf.setType(type);
    }

    public void dest_sourceIP(String[] s, Info inf) {
        String s1 = convertHexToInt(s[26]) + "." + convertHexToInt(s[27]) + "." + convertHexToInt(s[28]) + "." + convertHexToInt(s[29]);
        String s2 = convertHexToInt(s[30]) + "." + convertHexToInt(s[31]) + "." + convertHexToInt(s[32]) + "." + convertHexToInt(s[33]);
        rendu += "\t IP_Source : (" + s1 + ")\n";
        rendu += "\t IP_Dest : (" + s2 + ")\n";
        inf.setSourceIp(s1);
        inf.setDestinationIp(s2);
    }

    public void protocol(String[] s, Info inf) {
        String s1 = s[23];
        if (s1.equals("01")) {
            protocol = "ICMP";
        }
        if (s1.equals("02")) {
            protocol = "IGMP";
        }
        if (s1.equals("06")) {
            protocol = "TCP";
        }
        if (s1.equals("11")) {
            protocol = "UDP";
        }
        rendu += "\t Protocol : " + protocol + "\n";
        inf.setProtocol(protocol);
    }

    public void dest_sourcePort(String[] s, Info inf) {
        String s1 = s[opIP + 34] + s[opIP + 35];
        String s2 = s[opIP + 36] + s[opIP + 37];

        int convertedS1 = convertHexToInt(s1);
        int convertedS2 = convertHexToInt(s2);

        rendu += "\t Port_Source : " + convertedS1 + "\n";
        rendu += "\t Port_Dest : " + convertedS2 + "\n";
        inf.setSourcePort(String.valueOf(convertedS1));
        inf.setDestinationPort(String.valueOf(convertedS2));
        port_source = convertHexToInt(s1);
        port_dest = convertHexToInt(s2);
    }

    public void seq_ackNumber(String[] s, int i, Info inf) {
        rendu += "\t Seq : " + SN + "\n";
        rendu += "\t Ack : " + AN + "\n";
        inf.setSequenceNumber(Integer.toString(SN));
        inf.setAcknowledgmentNumber(Integer.toString(AN));

        if (port_source != port_prec) {
            int aux = SN;
            SN = AN;
            AN = aux;
            if(data==0){
                AN+=1;
            }
            else{
                AN += data;
            }
        }

        port_prec = port_source;
    }

    public void seq_ackRawNumber(String[] s, Info inf) {
        String s1 = s[opIP + 38] + s[opIP + 39] + s[opIP + 40] + s[opIP + 41];
        String s2 = s[opIP + 42] + s[opIP + 43] + s[opIP + 44] + s[opIP + 45];
        long l1 = convertHexToLong(s1);
        long l2 = convertHexToLong(s2);

        rendu += "\t SeqRaw : " + l1 + "\n";
        rendu += "\t AckRaw : " + l2 + "\n";
    }

    public void flags(String[] s, Info inf) {
        char c = s[opIP + 46].charAt(1);
        boolean virgule = false;
        rendu += "\t [";
        String flags = "";
        if (c == 1) {
            flags += "URG";
            virgule = true;

        }
        int i = Integer.parseInt(s[opIP + 47], 10);
        StringBuilder s1 = new StringBuilder(Integer.toBinaryString(i));
        while (s1.length() != 5) s1.insert(0, "0");

        if (s1.charAt(4) == '1') {
            if (virgule) {
                flags += ",";
            }
            flags += "FIN";
            virgule = true;
        }

        if (s1.charAt(3) == '1') {
            if (virgule) {
                flags += ",";
            }
            flags += "SYN";
            virgule = true;
        }

        if (s1.charAt(2) == '1') {
            if (virgule) {
                flags += ",";
            }
            flags += "RST";
            virgule = true;
        }

        if (s1.charAt(1) == '1') {
            if (virgule) {
                flags += ",";
            }
            flags += "PSH";
            virgule = true;
        }

        if (s1.charAt(0) == '1') {
            if (virgule) {
                flags += ",";
            }
            flags += "ACK";
            virgule = true;
        }

        rendu += flags + "]\n";
        inf.setFlags(flags);
    }

    public void fenetre(String[] s, Info inf) {
        String s1 = s[opIP + 48] + s[opIP + 49];
        long l1 = convertHexToLong(s1);
        rendu += "\t Window : " + l1 + "\n";
        inf.setWindow(Long.toString(l1));
    }

    public void tailleOptionsIP(String[] s, Info inf) {
        char c = s[14].charAt(1);
        opIP = Character.getNumericValue(c) * 4 - 20;
    }

    public void tailleOptionsTCP(String[] s, Info inf) {
        char c = s[opIP + 46].charAt(0);
        opTCP = Character.getNumericValue(c) * 4 - 20;
    }

    public void padding(String[] s, Info inf) {
        int i = s.length - 1;
        while (s[i].equals("00")) {
            i--;
        }
        debut_padding = i + 1;
        tailleData(s, inf);
    }

    public void tailleData(String[] s, Info inf) {
        data = debut_padding - 54 + opIP + opTCP;
    }

    public void httpSource_Dest(String[] s, Info inf) {
        String s1 = "";
        if (port_source == 80) {
            int i = 54 + opIP + opTCP;
            rendu += "\t";
            while(!s[i].equals("0D") && !s[i].equals("0d")){
                s1+=convertHextoString(s[i]);
                i++;
            }
            rendu += s1 +"\n";
            inf.setHttpContent(s1);
        } else {
            int i = 54 + opIP + opTCP;
            while(!s[i].equals("0D") && !s[i].equals("0d")){
                i++;
            }
            i+=8;
            while(!s[i].equals("0D") && !s[i].equals("0d")){ //Ajout du nom global du site
                s1+=convertHextoString(s[i]);
                i++;
            }
            int j = 54 + opIP + opTCP;
            while(!s[j].equals("20")){
                j++;
            }
            j+=1;
            while(!s[j].equals("20")){ //Ajout du repertoire particulier du site
                s1+=convertHextoString(s[j]);
                j++;
            }
            System.out.println(s1+"\n");
            rendu += s1 +"\n";
            inf.setHttpContent(s1);
        }
    }

    public boolean httpVerif(String [] s){
        String s1;
        if(port_source == 80){
            s1 = convertHextoString(s[54 + opIP + opTCP]) + convertHextoString(s[55 + opIP + opTCP]) + convertHextoString(s[56 + opIP + opTCP]) + convertHextoString(s[57 + opIP + opTCP]);
            System.out.println(s[54 + opIP + opTCP]+"\n");
            System.out.println(convertHextoString(s[54 + opIP + opTCP])+"\n");
            return s1.equals("HTTP");
        }
        else if (port_dest == 80) {
            s1 = convertHextoString(s[54 + opIP + opTCP]) + convertHextoString(s[55 + opIP + opTCP]) + convertHextoString(s[56 + opIP + opTCP]);
            System.out.println(s1+"\n");
            return s1.equals("GET");
        }
        return false;
    }

    public List<Info> getInfoList() { return this.infoList; }
}
