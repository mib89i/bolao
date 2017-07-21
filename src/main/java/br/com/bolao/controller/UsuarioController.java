/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.bolao.controller;

import br.com.bolao.dao.BolaoDao;
import br.com.bolao.dao.UsuarioDao;
import br.com.bolao.factory.Dao;
import br.com.bolao.model.Bolao;
import br.com.bolao.model.Grupo;
import br.com.bolao.model.GrupoUsuario;
import br.com.bolao.model.Ranking;
import br.com.bolao.model.Usuario;
import br.com.bolao.utils.MensagemFlash;
import br.com.bolao.utils.Sessao;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Claudemir Rtools
 */
@ManagedBean
@SessionScoped
public class UsuarioController implements Serializable {

    private Usuario usuario = new Usuario();
    private String sessaoMessageLogin = "";
    private String confirmeSenha = "";
    private Grupo grupo = new Grupo();
    private List<GrupoUsuario> listaGrupoUsuario = new ArrayList();
    private List<Object[]> listaUsuarioAdicionarGrupo = new ArrayList();

    private Part arquivo;
    private Boolean mobile = false;

//    public UsuarioController() {
//        try {
//            validacaoHeaderLogin();
//        } catch (IOException ex) {
//            Logger.getLogger(UsuarioController.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    public void loadPaginaCadastro() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if ((Sessao.exist("sessao_usuario")) || usuario.getId() != -1) {
                entrar();
                try {
                    FacesContext.getCurrentInstance().getExternalContext().redirect("ranking.xhtml");
                } catch (Exception e) {
                    e.getMessage();
                }
            } else {
                usuario = new Usuario();
                confirmeSenha = "";
            }
        }
    }

    public void loadPaginaGrupo() {
        loadListaGrupo();
    }

    public void loadListaUsuarioAdicionarGrupo(Grupo g) {
        grupo = g;
        UsuarioDao udao = new UsuarioDao();
        listaUsuarioAdicionarGrupo.clear();
        listaUsuarioAdicionarGrupo = udao.listaUsuarioMenosSessao(((Usuario) Sessao.get("sessao_usuario")).getId(), grupo.getId());
    }

    public void salvarAdicionarUsuarioGrupo(Integer id_usuario) {
        Dao dao = new Dao();
        BolaoDao bdao = new BolaoDao();
        dao.begin();
        GrupoUsuario gu = new GrupoUsuario(-1, grupo, (Usuario) dao.find(new Usuario(), id_usuario), false);
        if (!dao.save(gu)) {
            dao.rollback();
            MensagemFlash.fatal("", "Erro a adicionar jogador!");
            return;
        }

//        List<Bolao> lb = new BolaoDao().listaBolao(grupo.getId());
//        for (Bolao lbolao : lb) {
//            Object[] ultimo_ranking = bdao.pesquisaUltimoRankingDoBolao(lbolao.getId());
//            if (ultimo_ranking != null && ultimo_ranking[0] != null){
//                Ranking ran = new Ranking(
//                        -1,
//                        gu.getUsuario(),
//                        lbolao,
//                        0,
//                        (Integer) ultimo_ranking[0] + 1
//                );
//                
//                if (!dao.save(ran)){
//                    MensagemFlash.fatal("", "Erro a adicionar ranking!");
//                    return;
//                }
//            }
//        }
        dao.commit();
        loadListaUsuarioAdicionarGrupo(gu.getGrupo());
        ((BolaoController) Sessao.get("bolaoController")).loadListaUsuarioRankingGeral();
    }

    public void excluirAdicionarUsuarioGrupo(Integer id_usuario) {
        Dao dao = new Dao();
        UsuarioDao udao = new UsuarioDao();

        dao.begin();
        GrupoUsuario gu = udao.pesquisaGrupoUsuario(grupo.getId(), id_usuario);
        if (!dao.remove(dao.find(gu))) {
            dao.rollback();
            MensagemFlash.fatal("", "Erro a adicionar jogador!");
            return;
        }

//        BolaoDao bdao = new BolaoDao();
//        List<Bolao> lb = new BolaoDao().listaBolao(grupo.getId());
//        for (Bolao lbolao : lb) {
//            List<Ranking> listaRan = bdao.listaRankingBolao(lbolao.getId(), id_usuario);
//            for (Ranking ran : listaRan){
//                if (!dao.remove(dao.find(ran))){
//                    MensagemFlash.fatal("", "Erro ao remover ranking!");
//                    return;
//                }
//            }
//        }
        dao.commit();

        loadListaUsuarioAdicionarGrupo(gu.getGrupo());
        ((BolaoController) Sessao.get("bolaoController")).loadListaUsuarioRankingGeral();
    }

    public String salvarUsuario() {
        UsuarioDao udao = new UsuarioDao();

        if (usuario.getNome().isEmpty()) {
            MensagemFlash.fatal("", "DIGITE SEU NOME!");
            return null;
        }

        if (usuario.getEmail().isEmpty()) {
            MensagemFlash.fatal("", "DIGITE UM USUÁRIO PARA ESTA PESSOA!");
            return null;
        }

        usuario.setEmail(usuario.getEmail().toLowerCase());

        Usuario u = udao.pesquisaUsuarioLoginExiste(null, usuario.getEmail());
        if (u != null) {
            MensagemFlash.fatal("", "E-MAIL JÁ EXISTE, ESCOLHA OUTRO!");
            usuario.setEmail("");
            return null;
        }

        if (usuario.getSenha().isEmpty()) {
            MensagemFlash.fatal("", "DIGITE UMA SENHA DE USUÁRIO PARA ESTA PESSOA!");
            return null;
        }

        if (!usuario.getSenha().equals(confirmeSenha)) {
            MensagemFlash.fatal("", "SENHAS NÃO CORRESPONDEM!");
            return null;
        }

        Dao dao = new Dao();

        dao.begin();

        if (!dao.save(usuario)) {
            dao.rollback();
            MensagemFlash.fatal("", "ERRO AO SALVAR USUÁRIO!");
            return null;
        }

        dao.commit();

        entrar();
        return "ranking";
    }

    public void upload() {
        UUID uuidX = UUID.randomUUID();
        String nameTemp = uuidX.toString().replace("-", "_");
        try {
            ServletContext servletContext = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext());
            String path_criar = servletContext.getRealPath("/resources/images/usuario/" + usuario.getId());
            if (!new File(path_criar).exists()) {
                File file = new File(path_criar);
                if (!file.mkdirs()) {
                    return;
                }
            }

            String path_upload = servletContext.getRealPath("") + "resources/images/usuario/" + usuario.getId() + "/" + nameTemp + ".png";
            File file = new File(path_upload);

            byte[] bytes = IOUtils.toByteArray(arquivo.getInputStream());

            try {
                FileUtils.writeByteArrayToFile(file, bytes);
            } catch (IOException e) {
                e.getMessage();
            }

            usuario.setFoto(nameTemp + ".png");
        } catch (IOException e) {
            e.getMessage();
        }
    }

    public String criarGrupo() {
        grupo = new Grupo();
        loadListaGrupo();
        return "grupo";
    }

    public String editarGrupo(GrupoUsuario gu) {
        grupo = gu.getGrupo();
        return "grupo";
    }

    public void salvarGrupo() {
        if (grupo.getNome().isEmpty()) {
            MensagemFlash.error("", "Digite um nome para o Grupo");
            return;
        }
        Dao dao = new Dao();

        dao.begin();

        if (grupo.getId() == -1) {
            //List<GrupoUsuario> result = new BolaoDao().listaGrupoUsuario(usuario.getId());
//            if (result.size() > 0) {
//                dao.rollback();
//                MensagemFlash.error("", "Você já esta em um grupo");
//                return;
//            }
            if (!dao.save(grupo)) {
                dao.rollback();
                MensagemFlash.error("", "Erro ao Salvar Grupo");
                return;
            }

            GrupoUsuario gu = new GrupoUsuario(-1, grupo, usuario, true);
            if (!dao.save(gu)) {
                dao.rollback();
                MensagemFlash.error("", "Erro ao Salvar Grupo Usuário");
                return;
            }
            MensagemFlash.info("", "Grupo Salvo");
        } else {
            if (!dao.update(grupo)) {
                dao.rollback();
                MensagemFlash.error("", "Erro ao Salvar Grupo");
                return;
            }
            MensagemFlash.info("", "Grupo Atualizado");
        }

        dao.commit();
        grupo = new Grupo();
    }

    public void excluirGrupo() {
        BolaoDao bdao = new BolaoDao();
        if (!bdao.listaBolao(grupo.getId()).isEmpty()) {
            MensagemFlash.error("", "Existem bolões para este grupo, não pode ser excluído!");
            return;
        }

        Dao dao = new Dao();

        dao.begin();

        List<GrupoUsuario> result = new BolaoDao().listaGrupoUsuarioPorGrupo(grupo.getId());
        for (GrupoUsuario gu : result) {
            if (!dao.remove(dao.find(gu))) {
                dao.rollback();
                MensagemFlash.error("", "Erro ao Excluir Grupo Usuário");
                return;
            }
        }

        if (!dao.remove(dao.find(grupo))) {
            dao.rollback();
            MensagemFlash.error("", "Erro ao Excluir Grupo");
            return;
        }

        dao.commit();

        if (grupo.getId() == ((BolaoController) Sessao.get("bolaoController")).getGrupoUsuarioSelecionado().getGrupo().getId()) {
            ((BolaoController) Sessao.get("bolaoController")).setGrupoUsuarioSelecionado(new GrupoUsuario());
            ((BolaoController) Sessao.get("bolaoController")).loadListaGrupoUsuario();
        }

        grupo = new Grupo();
        loadListaGrupo();
        MensagemFlash.info("", "Grupo Excluído");
    }

    public void loadListaGrupo() {
        listaGrupoUsuario.clear();
        listaGrupoUsuario = new BolaoDao().listaGrupoUsuario(usuario.getId());
    }

    public String entrar() {
        limpar_sessao_antes_de_iniciar();

        UsuarioDao dao = new UsuarioDao();

        Usuario u = dao.pesquisaUsuario(usuario.getEmail(), usuario.getSenha());

        if (u != null) {
            Sessao.put("sessao_usuario", u);
            Object redirect_page = Sessao.get("sessao_redirect_page", true);

            sessaoMessageLogin = "";
            usuario = u;

            if (redirect_page != null) {
                return redirect_page.toString().replace(".xhtml", "");
            } else {
                return "ranking";
            }
        } else {
            MensagemFlash.fatal("", "USUÁRIO E/OU SENHA INVÁLIDOS");
            return null;
        }
    }

    public void sair() {
        try {
            FacesContext conext = FacesContext.getCurrentInstance();
            //Verifica a sessao e a grava na variavel
            HttpSession session = (HttpSession) conext.getExternalContext().getSession(false);
            //Fecha/Destroi sessao
            session.invalidate();
            validacaoHeaderLogin();
            if (mobile) {
                FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml?mobile=true");
            } else {
                FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
            }
        } catch (Exception e) {

        }
    }

    public void limpar_sessao_antes_de_iniciar() {

    }

    public void validacao() throws IOException {
        HttpServletRequest pagina_requerida = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

        String uri_pagina = pagina_requerida.getRequestURI().replace("/bolao/", "");

        if (!Sessao.exist("sessao_usuario")) {
            Sessao.put("sessao_redirect_page", uri_pagina);
            Sessao.put("sessao_message_login", "FAÇA O LOGIN PARA TER ACESSO A ESSA PÁGINA");

            FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
        }

        if (mobile == false) {
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            String mobilex = request.getParameter("mobile");
            mobile = mobilex != null;
        }
    }

    public final void validacaoHeaderLogin() throws IOException {
        if (mobile == false) {
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            String mobilex = request.getParameter("mobile");
            mobile = mobilex != null;
        }
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getSessaoMessageLogin() {
        if (Sessao.exist("sessao_message_login")) {
            sessaoMessageLogin = (String) Sessao.get("sessao_message_login", true);
        }
        return sessaoMessageLogin;
    }

    public void setSessaoMessageLogin(String sessaoMessageLogin) {
        this.sessaoMessageLogin = sessaoMessageLogin;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public List<GrupoUsuario> getListaGrupoUsuario() {
        return listaGrupoUsuario;
    }

    public void setListaGrupoUsuario(List<GrupoUsuario> listaGrupoUsuario) {
        this.listaGrupoUsuario = listaGrupoUsuario;
    }

    public Part getArquivo() {
        return arquivo;
    }

    public void setArquivo(Part arquivo) {
        this.arquivo = arquivo;
    }

    public String getConfirmeSenha() {
        return confirmeSenha;
    }

    public void setConfirmeSenha(String confirmeSenha) {
        this.confirmeSenha = confirmeSenha;
    }

    public List<Object[]> getListaUsuarioAdicionarGrupo() {
        return listaUsuarioAdicionarGrupo;
    }

    public void setListaUsuarioAdicionarGrupo(List<Object[]> listaUsuarioAdicionarGrupo) {
        this.listaUsuarioAdicionarGrupo = listaUsuarioAdicionarGrupo;
    }

    public Boolean getMobile() {
        return mobile;
    }

    public void setMobile(Boolean mobile) {
        this.mobile = mobile;
    }

}
