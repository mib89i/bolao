package br.com.bolao.utils;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean
@ViewScoped
public class MensagemFlash implements Serializable {

    private static String ALERT = "";

    public static void error(String title, String description) {
        ALERT = "alert-danger";
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, title, description));
    }

    public static void fatal(String title, String description) {
        ALERT = "alert-danger";
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, title, description));
    }

    public static void info(String title, String description) {
        ALERT = "alert-info";
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, title, description));
    }

    public static void warn(String title, String description) {
        ALERT = "alert-warning";
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, title, description));
    }

    public String getALERT() {
        return ALERT;
    }
}
