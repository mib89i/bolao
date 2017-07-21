package br.com.bolao.model;

import java.io.File;
import java.io.Serializable;
import javax.faces.context.FacesContext;
import javax.persistence.*;
import javax.servlet.ServletContext;

@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "email", length = 255)
    private String email;
    @Column(name = "senha", length = 255)
    private String senha;
    @Column(name = "nome", length = 500)
    private String nome;
    @Column(name = "apelido", length = 500)
    private String apelido;
    @Column(name = "foto", length = 500)
    private String foto;
    @Column(name = "administrador")
    private Boolean administrador;
    @Transient
    private String caminhoFoto;
    
    public Usuario() {
        this.id = -1;
        this.email = "";
        this.senha = "";
        this.nome = "";
        this.apelido = "";
        this.foto = "";
        this.administrador = false;
    }

    public Usuario(int id, String usuario, String senha, String nome, String apelido, String foto, Boolean administrador) {
        this.id = id;
        this.email = usuario;
        this.senha = senha;
        this.nome = nome;
        this.apelido = apelido;
        this.foto = foto;
        this.administrador = administrador;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Boolean getAdministrador() {
        return administrador;
    }

    public void setAdministrador(Boolean administrador) {
        this.administrador = administrador;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
    
    public String getCaminhoFoto(){
        ServletContext servletContext = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext());
        if ( (this.id == -1 && this.foto.isEmpty()) || (this.id != -1 && this.foto.isEmpty())){
            return caminhoFoto = "/resources/images/sem_foto.png";
        }
        
        if (this.id == -1 && !this.foto.isEmpty()){
            String path_foto = servletContext.getRealPath("/resources/images/usuario/-1/" + this.foto);
            if (new File(path_foto).exists()) {
                return caminhoFoto = "/resources/images/usuario/-1/" + this.foto;
            } else {
                return caminhoFoto = "/resources/images/sem_foto.png";
            }
        }
        
        if (this.id != -1 && !this.foto.isEmpty()){
            String path_foto = servletContext.getRealPath("/resources/images/usuario/" + this.id + "/" + this.foto);
            if (new File(path_foto).exists()) {
                return caminhoFoto = "/resources/images/usuario/" + this.id + "/"  + this.foto;
            } else {
                return caminhoFoto = "/resources/images/sem_foto.png";
            }
        }
        return caminhoFoto;
    }
    
    public void setCaminhoFoto(String caminhoFoto){
        this.caminhoFoto = caminhoFoto;
    }    
}
