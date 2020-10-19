package com.alfredo.dividirred.models;

public class SubRed {
    private String ipinicial,ipfinal,mascara, nodos,descripcion;
    public SubRed(String ipinicial,String ipfianal, String mascara,String nodos,String descripcion){
        this.ipfinal = ipfianal;
        this.ipinicial = ipinicial;
        this.mascara = mascara;
        this.nodos = nodos;
        this.descripcion = descripcion;
    }

    public String getIpfinal() {
        return ipfinal;
    }

    public String getIpinicial() {
        return ipinicial;
    }

    public String getMascara() {
        return mascara;
    }

    public String getNodos() {
        return nodos;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
