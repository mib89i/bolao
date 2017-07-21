/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.bolao.controller;

import br.com.bolao.dao.BolaoDao;
import br.com.bolao.factory.Dao;
import br.com.bolao.model.Bolao;
import br.com.bolao.model.Grupo;
import br.com.bolao.model.GrupoUsuario;
import br.com.bolao.model.Jogo;
import br.com.bolao.model.Palpite;
import br.com.bolao.model.Pontuacao;
import br.com.bolao.model.Ranking;
import br.com.bolao.model.Time;
import br.com.bolao.model.TipoPalpite;
import br.com.bolao.model.Usuario;
import br.com.bolao.utils.Datas;
import br.com.bolao.utils.MensagemFlash;
import br.com.bolao.utils.Sessao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author Claudemir Rtools
 */
@ManagedBean
@SessionScoped
public class BolaoController implements Serializable {

    private Bolao bolao = new Bolao();
    private Jogo jogo = new Jogo();
    private GrupoUsuario grupoUsuarioSelecionado = new GrupoUsuario();
    private List<Jogo> listaJogo = new ArrayList();
    private List<GrupoUsuario> listaGrupoUsuario = new ArrayList();
    private List<BolaoPalpite> listaBolaoPalpites = new ArrayList(); // apagar

    private List<Bolao> listaBolao = new ArrayList();
    private List<PalpiteStatus> listaPalpiteStatus = new ArrayList();

    private List<UsuarioRankingGeral> listaUsuarioRankingGeral = new ArrayList();
    private List<UsuarioRankingBolao> listaUsuarioRankingBolao = new ArrayList();

    private List<Pontuacao> listaPontuacao = new ArrayList();

    //private List<UsuarioPontos> listaUsuarioPontos = new ArrayList();
    private List<Time> listaPesquisaTime = new ArrayList();
    private String pesquisaTime1 = "";
    private String pesquisaTime2 = "";

    public BolaoController() {
        loadListaPontuacao();
    }

    public void loadPesquisaTime(Long n) {
        listaPesquisaTime.clear();
        if (n == 1) {
            jogo.setTime1(new Time());
            pesquisaTime2 = "";
            if (!pesquisaTime1.isEmpty()) {
                listaPesquisaTime = new BolaoDao().listaPesquisaTime(pesquisaTime1);
            }
        } else if (!pesquisaTime2.isEmpty()) {
            jogo.setTime2(new Time());
            pesquisaTime1 = "";
            listaPesquisaTime = new BolaoDao().listaPesquisaTime(pesquisaTime2);
        }
    }

    public void selecionarTime(Time t) {
        if (!pesquisaTime1.isEmpty()) {
            jogo.setTime1(t);
        }

        if (!pesquisaTime2.isEmpty()) {
            jogo.setTime2(t);
        }

        listaPesquisaTime.clear();
        pesquisaTime1 = "";
        pesquisaTime2 = "";
    }

    public void selecionarOutroTime(Long n) {
        if (n == 1) {
            jogo.setTime1(new Time());
        }

        if (n == 2) {
            jogo.setTime2(new Time());
        }
    }
    
    public final void loadPaginaRanking() {
        if (Sessao.exist("sessao_usuario")) {
            if (!FacesContext.getCurrentInstance().isPostback()) {
                loadListaGrupoUsuario();
                loadListaBolaoPalpite();
                loadListaBolao();
                loadListaUsuarioRankingGeral();
                //loadListaUsuarioPontos();
            }
        }
    }

    public final void loadListaPontuacao() {
        listaPontuacao.clear();
        BolaoDao bdao = new BolaoDao();

        listaPontuacao = bdao.listaPontuacao();
    }

    public final void loadListaBolao() {
        if (grupoUsuarioSelecionado.getId() != -1) {
            listaBolao.clear();
            BolaoDao bdao = new BolaoDao();
            listaBolao = bdao.listaBolaoGrupo(grupoUsuarioSelecionado.getGrupo().getId());
        }
    }

    public final void loadListaPalpiteStatus(Integer id_bolao) {
        BolaoDao bdao = new BolaoDao();
        listaPalpiteStatus.clear();

        List<Jogo> result = bdao.listaJogo(id_bolao);

        for (Jogo jogox : result) {
            Palpite p = bdao.pesquisaPalpite(jogox.getId(), ((Usuario) Sessao.get("sessao_usuario")).getId());
            String status;

            if (p != null) {
                status = retornaStatusPalpite(p, jogox, bdao);
                listaPalpiteStatus.add(new PalpiteStatus(p, status));
            } else {
                status = retornaStatusPalpite(null, jogox, bdao);
                p = new Palpite();
                p.setJogo(jogox);
                listaPalpiteStatus.add(new PalpiteStatus(p, status));
            }
        }
    }

    public final void loadListaUsuarioPontuacaoBolao(Integer id_bolao) {
        Dao dao = new Dao();
        BolaoDao bdao = new BolaoDao();
        bolao = (Bolao) new Dao().find(new Bolao(), id_bolao);

        listaUsuarioRankingBolao.clear();

        List<Object[]> result = bdao.listaUsuarioRankingBolao(id_bolao);
        for (Object[] list : result) {
            listaUsuarioRankingBolao.add(
                    new UsuarioRankingBolao(
                            (Usuario) dao.find(new Usuario(), list[0]), // USUÁRIO
                            (Integer) list[1], // PONTOS
                            (Integer) list[2], // QUANTIDADE DE PALPITES
                            (Integer) list[3] // RANKING
                    )
            );
        }

        loadListaPalpiteStatus(id_bolao);
    }

    public String selecionarGrupoUsuario(GrupoUsuario gu) {
        grupoUsuarioSelecionado = gu;
        Sessao.put("grupo_usuario_selecionado", grupoUsuarioSelecionado);
        loadListaBolaoPalpite();
        return "ranking";
    }

    public void salvarPalpites() {
        Dao dao = new Dao();
        BolaoDao bdao = new BolaoDao();

        if (!listaPalpiteStatus.isEmpty()) {
            dao.begin();
            for (PalpiteStatus palps : listaPalpiteStatus) {
                if (palps.getPalpite().getId() == -1) {
                    if (palps.getPalpite().getPlacarTime1() == null || palps.getPalpite().getPlacarTime2() == null) {
                        continue;
                    }

                    if (!retornaStatusPalpite(null, palps.getPalpite().getJogo(), bdao).isEmpty()) {
                        continue;
                    }

                    TipoPalpite tp = retornaTipoPalpite(palps.getPalpite());
                    palps.getPalpite().setTipoPalpite(tp);

                    palps.getPalpite().setDataPalpite(Datas.dataHoje());
                    palps.getPalpite().setHoraPalpite(Datas.hora());
                    palps.getPalpite().setUsuario((Usuario) Sessao.get("sessao_usuario"));
                    if (!dao.save(palps.getPalpite())) {
                        dao.rollback();
                        MensagemFlash.error("", "Erro ao Salvar Palpite!");
                        return;
                    }
                } else {
                    if (palps.getPalpite().getPlacarTime1() == null || palps.getPalpite().getPlacarTime2() == null) {
                        if (!dao.remove(dao.find(palps.getPalpite()))) {
                            dao.rollback();
                            MensagemFlash.error("", "Erro ao Atualizar Palpite!");
                            return;
                        }
                        continue;
                    }
                    Palpite p = bdao.pesquisaPalpiteID(palps.getPalpite().getId());
                    if (!retornaStatusPalpite(p, p.getJogo(), bdao).isEmpty()) {
                        continue;
                    }

                    TipoPalpite tp = retornaTipoPalpite(palps.getPalpite());
                    palps.getPalpite().setTipoPalpite(tp);

                    palps.getPalpite().setDataAtualizacao(Datas.dataHoje());
                    palps.getPalpite().setHoraAtualizacao(Datas.hora());
                    if (!dao.update(palps.getPalpite())) {
                        dao.rollback();
                        MensagemFlash.error("", "Erro ao Atualizar Palpite!");
                        return;
                    }
                }
            }

            dao.commit();

            loadListaUsuarioPontuacaoBolao(bolao.getId());
            loadListaUsuarioRankingGeral();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(BolaoController.class.getName()).log(Level.SEVERE, null, ex);
            }

            MensagemFlash.info("", "Palpites Salvo!");
        }
    }

    public TipoPalpite retornaTipoPalpite(Palpite p) {
        if (p.getPlacarTime1() > p.getPlacarTime2() || p.getPlacarTime1() < p.getPlacarTime2()) {
            return (TipoPalpite) new Dao().find(new TipoPalpite(), 1);
        } else {
            return (TipoPalpite) new Dao().find(new TipoPalpite(), 2);
        }
    }

    public String novoBolao(Integer id_grupo) {
        bolao = new Bolao();
        bolao.setGrupo((Grupo) new Dao().find(new Grupo(), id_grupo));

        loadListaJogo();
        return "bolao";
    }

    public String editarBolao(Integer id_bolao) {
        bolao = (Bolao) new Dao().find(new Bolao(), id_bolao);
        jogo = new Jogo();
        loadListaJogo();
        return "bolao";
    }

    public void salvarBolao() {
        if (bolao.getNome().isEmpty()) {
            MensagemFlash.error("", "Digite um nome para o Bolão!");
            return;
        }

        if (bolao.getGrupo().getId() == -1) {
            bolao.setGrupo(getGrupoUsuarioSelecionado().getGrupo());
        }

        Dao dao = new Dao();

        dao.begin();
        if (bolao.getId() == -1) {
            if (!dao.save(bolao)) {
                dao.rollback();
                MensagemFlash.error("", "Erro ao Adicionar!");
                return;
            }
        } else if (!dao.update(bolao)) {
            dao.rollback();
            MensagemFlash.error("", "Erro ao Atualizar!");
            return;
        }
        dao.commit();
        MensagemFlash.info("", "Bolão Salvo!");
    }

    public void excluirBolao() {
        Dao dao = new Dao();

        if (bolao.getId() != -1) {
            dao.begin();
            if (!dao.remove(dao.find(bolao))) {
                dao.rollback();

                dao.begin();
                bolao.setAtivo(false);
                if (!dao.update(bolao)) {
                    dao.rollback();
                    MensagemFlash.info("", "Bolão não pode ser excluído!");
                    return;
                }
            }
        }
        dao.commit();
        bolao = new Bolao();
        loadListaJogo();
        MensagemFlash.info("", "Bolão Excluído!");
    }

    public void adicionarJogo() {
        if (bolao.getId() == -1) {
            salvarBolao();
            if (bolao.getId() == -1) {
                MensagemFlash.error("", "Bolão não foi salvo.");
                return;
            }
        }

        if (jogo.getDataJogoString().isEmpty()) {
            MensagemFlash.error("", "Digite uma data para o Jogo.");
            return;
        }

        if (jogo.getTime1().getId() == -1) {
            MensagemFlash.error("", "Pesquise o Time 1.");
            return;
        }

        if (jogo.getTime2().getId() == -1) {
            MensagemFlash.error("", "Pesquise o Time 2.");
            return;
        }

        if (jogo.getLocal().isEmpty()) {
            jogo.setLocal("NÃO ESPECIFICADO");
        }

        if (jogo.getImportancia() == null) {
            jogo.setImportancia(1);
        }

        Dao dao = new Dao();

        dao.begin();

        BolaoDao bdao = new BolaoDao();

//        jogo.getTime1().setNome(jogo.getTime1().getNome().trim());
//        Time time1 = (Time) bdao.pesquisaTime(jogo.getTime1().getNome());
//        if (time1 == null) {
//            if (!dao.save(jogo.getTime1())) {
//                dao.rollback();
//                MensagemFlash.error("", "Erro ao Salvar Jogo!");
//                return;
//            }
//        } else {
//            jogo.setTime1(time1);
//        }
//
//        jogo.getTime2().setNome(jogo.getTime2().getNome().trim());
//        Time time2 = (Time) bdao.pesquisaTime(jogo.getTime2().getNome());
//        if (time2 == null) {
//            if (!dao.save(jogo.getTime2())) {
//                dao.rollback();
//                MensagemFlash.error("", "Erro ao Salvar Jogo!");
//                return;
//            }
//        } else {
//            jogo.setTime2(time2);
//        }

        if (jogo.getId() == -1) {
            jogo.setBolao(bolao);

            if (!dao.save(jogo)) {
                dao.rollback();
                MensagemFlash.error("", "Erro ao Salvar Jogo!");
                return;
            }
            MensagemFlash.info("", "Jogo Adicionado!");
        } else {
            if (!dao.update(jogo)) {
                dao.rollback();
                MensagemFlash.error("", "Erro ao Salvar Jogo!");
                return;
            }
            MensagemFlash.info("", "Jogo Atualizado!");
        }

        dao.commit();

        jogo = new Jogo();
        loadListaJogo();
    }

    public void editarJogo(Jogo j) {
        jogo = j;
    }

    public void alterarJogo(Jogo j) {
        Dao dao = new Dao();

        dao.begin();

        if (!dao.update(j)) {
            dao.rollback();
            MensagemFlash.error("", "Erro ao Alterar Jogo!");
            return;
        }

        dao.commit();
        MensagemFlash.info("", "Jogo Atualizado!");
    }

    public void excluirJogo(Jogo j) {
        Dao dao = new Dao();
        BolaoDao bdao = new BolaoDao();

        dao.begin();

        List<Palpite> result = bdao.listaPalpite(j.getId());
        for (Palpite palp : result) {
            if (!dao.remove(dao.find(palp))) {
                dao.rollback();
                MensagemFlash.error("", "Erro ao Excluir Palpite!");
                return;
            }
        }

        if (!dao.remove(dao.find(j))) {
            dao.rollback();
            MensagemFlash.error("", "Erro ao Excluir Jogo!");
            return;
        }
        MensagemFlash.info("", "Jogo Removido!");

        dao.commit();
        loadListaJogo();
    }

    public void finalizarJogo(Jogo j) {
        if (j.getPlacarTime1() == null) {
            MensagemFlash.error("", "Digite os Placares para Finalizar este jogo!");
            return;
        }

        if (j.getPlacarTime2() == null) {
            MensagemFlash.error("", "Digite os Placares para Finalizar este jogo!");
            return;
        }
        Dao dao = new Dao();

        dao.begin();
        j.setDataJogoFinal(Datas.dataHoje());
        j.setHoraJogoFinal(Datas.horaMinuto());

        if (!dao.update(j)) {
            dao.rollback();
            MensagemFlash.error("", "Erro ao Finalizar Jogo!");
            return;
        }

        dao.commit();
        MensagemFlash.info("", "Jogo Finalizado!");
    }

    public void finalizarBolao() {
        if (bolao.getId() == -1) {
            MensagemFlash.error("", "Nenhum Bolão para ser Finalizado!");
            return;
        }

        List<Jogo> result = new BolaoDao().listaJogoAberto(bolao.getId());

        if (!result.isEmpty()) {
            MensagemFlash.error("", "Ainda existem jogos em Aberto!");
            return;
        }

        Dao dao = new Dao();

        dao.begin();
        bolao.setFinalizado(true);

        if (!dao.update(bolao)) {
            dao.rollback();
            MensagemFlash.error("", "Erro ao atualizar bolão!");
            return;
        }

        BolaoDao bdao = new BolaoDao();
        List<Object[]> lsita_br = bdao.listaUsuarioRankingBolao(bolao.getId());

        for (int i = 0; i < lsita_br.size(); i++) {
            Object[] list = lsita_br.get(i);
            Ranking ran = new Ranking(
                    -1,
                    (Usuario) dao.find(new Usuario(), list[0]),
                    bolao,
                    (Integer) list[1],
                    i + 1
            );

            if (!dao.save(ran)) {
                dao.rollback();
                MensagemFlash.error("", "Erro ao salvar ranking!");
                return;
            }
        }

        dao.commit();
        loadListaBolaoPalpite();
        loadListaUsuarioRankingGeral();
        MensagemFlash.info("", "Bolão Finalizado!");
    }

    public void loadListaJogo() {
        listaJogo.clear();
        if (bolao.getId() == -1) {
            return;
        }

        listaJogo = new BolaoDao().listaJogo(bolao.getId());
    }

    public final void loadListaGrupoUsuario() {
        listaGrupoUsuario.clear();

        listaGrupoUsuario = new BolaoDao().listaGrupoUsuario(((Usuario) Sessao.get("sessao_usuario")).getId());

        if (listaGrupoUsuario.size() == 1 && grupoUsuarioSelecionado.getId() == -1) {
            grupoUsuarioSelecionado = listaGrupoUsuario.get(0);
        }
    }

    public void loadListaBolaoPalpite() {
        if (grupoUsuarioSelecionado.getId() != -1) {
            BolaoDao bdao = new BolaoDao();
            listaBolaoPalpites.clear();
            List<Bolao> lb = new BolaoDao().listaBolao(grupoUsuarioSelecionado.getGrupo().getId());
            for (Bolao lbolao : lb) {
                List<Jogo> result = new BolaoDao().listaJogo(lbolao.getId());
                List<PalpiteStatus> lp = new ArrayList();
                for (Jogo jogox : result) {
                    Palpite p = bdao.pesquisaPalpite(jogox.getId(), ((Usuario) Sessao.get("sessao_usuario")).getId());
                    String status;

                    if (p != null) {
                        status = retornaStatusPalpite(p, jogox, bdao);

                        lp.add(new PalpiteStatus(p, status));
                    } else {
                        status = retornaStatusPalpite(null, jogox, bdao);

                        p = new Palpite();
                        p.setJogo(jogox);
                        lp.add(new PalpiteStatus(p, status));
                    }
                }

                listaBolaoPalpites.add(new BolaoPalpite(lbolao, lp));
            }
        }
    }

    public String retornaStatusPalpite(Palpite p, Jogo j, BolaoDao bdao) {
        String status = "";
        if (!j.getDataJogoFinalString().isEmpty() && !j.getHoraJogoFinal().isEmpty()) {
            status = "FINAL " + j.getTime1().getNome() + " " + j.getPlacarTime1();
            status += " x " + j.getPlacarTime2() + " " + j.getTime2().getNome();
            if (p != null) {
                status += " (" + new BolaoDao().pontuacaoPalpite(p.getId()) + ")";
            } else {
                status += " ( NÃO APOSTOU ) ";
            }
        } else if (bdao.pesquisaJogoDisponivelPalpite(j.getId(), Datas.data(), Datas.horaMinuto()) == null) {
            if (p == null) {
                status = "VOCÊ NÃO APOSTOU NESTE JOGO";
            } else {
                status = "JOGO EM ANDAMENTO";
            }
        }
        return status;
    }

    public void loadListaUsuarioRankingGeral() {
        // select rank() over (order by nome asc) as rank, nome from usuario
        Dao dao = new Dao();
        BolaoDao bdao = new BolaoDao();

        listaUsuarioRankingGeral.clear();

        List<Object[]> result = bdao.listaUsuarioRankingGeralGrupoQuery(grupoUsuarioSelecionado.getGrupo().getId());
        for (Object[] list : result) {
            listaUsuarioRankingGeral.add(
                    new UsuarioRankingGeral(
                            (Usuario) dao.find(new Usuario(), list[0]), // USUÁRIO
                            (Integer) list[1], // POSIÇÃO ANTERIOR
                            (Integer) list[6], // POSIÇÃO ATUAL
                            (Integer) list[2], // PONTOS ANTERIOR
                            (Integer) list[3], // PONTUACAO GERAL ATUAL
                            (Integer) list[4], // VARIACAO
                            (Integer) list[5] // QUANTIDADE DE PALPITES
                    )
            );
        }

    }

    public Bolao getBolao() {
        return bolao;
    }

    public void setBolao(Bolao bolao) {
        this.bolao = bolao;
    }

    public Jogo getJogo() {
        return jogo;
    }

    public void setJogo(Jogo jogo) {
        this.jogo = jogo;
    }

    public List<Jogo> getListaJogo() {
        return listaJogo;
    }

    public void setListaJogo(List<Jogo> listaJogo) {
        this.listaJogo = listaJogo;
    }

    public List<GrupoUsuario> getListaGrupoUsuario() {
        return listaGrupoUsuario;
    }

    public void setListaGrupoUsuario(List<GrupoUsuario> listaGrupoUsuario) {
        this.listaGrupoUsuario = listaGrupoUsuario;
    }

    public List<BolaoPalpite> getListaBolaoPalpites() {
        return listaBolaoPalpites;
    }

    public void setListaBolaoPalpites(List<BolaoPalpite> listaBolaoPalpites) {
        this.listaBolaoPalpites = listaBolaoPalpites;
    }
//
//    public List<UsuarioPontos> getListaUsuarioPontos() {
//        return listaUsuarioPontos;
//    }
//
//    public void setListaUsuarioPontos(List<UsuarioPontos> listaUsuarioPontos) {
//        this.listaUsuarioPontos = listaUsuarioPontos;
//    }

    public List<UsuarioRankingGeral> getListaUsuarioRankingGeral() {
        return listaUsuarioRankingGeral;
    }

    public void setListaUsuarioRankingGeral(List<UsuarioRankingGeral> listaUsuarioRankingGeral) {
        this.listaUsuarioRankingGeral = listaUsuarioRankingGeral;
    }

    public GrupoUsuario getGrupoUsuarioSelecionado() {
        if (grupoUsuarioSelecionado.getId() == -1 && Sessao.exist("grupo_usuario_selecionado")) {
            grupoUsuarioSelecionado = (GrupoUsuario) Sessao.get("grupo_usuario_selecionado");
        }
        return grupoUsuarioSelecionado;
    }

    public void setGrupoUsuarioSelecionado(GrupoUsuario grupoUsuarioSelecionado) {
        this.grupoUsuarioSelecionado = grupoUsuarioSelecionado;
    }

    public List<UsuarioRankingBolao> getListaUsuarioRankingBolao() {
        return listaUsuarioRankingBolao;
    }

    public void setListaUsuarioRankingBolao(List<UsuarioRankingBolao> listaUsuarioRankingBolao) {
        this.listaUsuarioRankingBolao = listaUsuarioRankingBolao;
    }

    public List<Bolao> getListaBolao() {
        return listaBolao;
    }

    public void setListaBolao(List<Bolao> listaBolao) {
        this.listaBolao = listaBolao;
    }

    public List<PalpiteStatus> getListaPalpiteStatus() {
        return listaPalpiteStatus;
    }

    public void setListaPalpiteStatus(List<PalpiteStatus> listaPalpiteStatus) {
        this.listaPalpiteStatus = listaPalpiteStatus;
    }

    public List<Pontuacao> getListaPontuacao() {
        return listaPontuacao;
    }

    public void setListaPontuacao(List<Pontuacao> listaPontuacao) {
        this.listaPontuacao = listaPontuacao;
    }

    public List<Time> getListaPesquisaTime() {
        return listaPesquisaTime;
    }

    public void setListaPesquisaTime(List<Time> listaPesquisaTime) {
        this.listaPesquisaTime = listaPesquisaTime;
    }

    public String getPesquisaTime1() {
        return pesquisaTime1;
    }

    public void setPesquisaTime1(String pesquisaTime1) {
        this.pesquisaTime1 = pesquisaTime1;
    }

    public String getPesquisaTime2() {
        return pesquisaTime2;
    }

    public void setPesquisaTime2(String pesquisaTime2) {
        this.pesquisaTime2 = pesquisaTime2;
    }

    public class BolaoPalpite implements Serializable {

        private Bolao bolao;
        private List<PalpiteStatus> listaPalpiteStatus;

        public BolaoPalpite(Bolao bolao, List<PalpiteStatus> listaPalpiteStatus) {
            this.bolao = bolao;
            this.listaPalpiteStatus = listaPalpiteStatus;
        }

        public Bolao getBolao() {
            return bolao;
        }

        public void setBolao(Bolao bolao) {
            this.bolao = bolao;
        }

        public List<PalpiteStatus> getListaPalpiteStatus() {
            return listaPalpiteStatus;
        }

        public void setListaPalpiteStatus(List<PalpiteStatus> listaPalpiteStatus) {
            this.listaPalpiteStatus = listaPalpiteStatus;
        }

    }

    public class PalpiteStatus implements Serializable {

        private Palpite palpite;
        private String status;

        public PalpiteStatus(Palpite palpite, String status) {
            this.palpite = palpite;
            this.status = status;
        }

        public Palpite getPalpite() {
            return palpite;
        }

        public void setPalpite(Palpite palpite) {
            this.palpite = palpite;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

    }

    public class UsuarioPontos implements Serializable {

        private Usuario usuario;
        private Integer pontos;

        public UsuarioPontos(Usuario usuario, Integer pontos) {
            this.usuario = usuario;
            this.pontos = pontos;
        }

        public Usuario getUsuario() {
            return usuario;
        }

        public void setUsuario(Usuario usuario) {
            this.usuario = usuario;
        }

        public Integer getPontos() {
            return pontos;
        }

        public void setPontos(Integer pontos) {
            this.pontos = pontos;
        }

    }

    public class UsuarioRankingGeral implements Serializable {

        private Usuario usuario;
        private Integer posicaoAnterior;
        private Integer posicaoAtual;
        private Integer pontuacaoAnterior;
        private Integer pontuacaoAtual;
        private Integer variacao;
        private Integer quantidadePalpites;

        public UsuarioRankingGeral(Usuario usuario, Integer posicaoAnterior, Integer posicaoAtual, Integer pontuacaoAnterior, Integer pontuacaoAtual, Integer variacao, Integer quantidadePalpites) {
            this.usuario = usuario;
            this.posicaoAnterior = posicaoAnterior;
            this.posicaoAtual = posicaoAtual;
            this.pontuacaoAnterior = pontuacaoAnterior;
            this.pontuacaoAtual = pontuacaoAtual;
            this.variacao = variacao;
            this.quantidadePalpites = quantidadePalpites;
        }

        public Usuario getUsuario() {
            return usuario;
        }

        public void setUsuario(Usuario usuario) {
            this.usuario = usuario;
        }

        public Integer getPosicaoAnterior() {
            return posicaoAnterior;
        }

        public void setPosicaoAnterior(Integer posicaoAnterior) {
            this.posicaoAnterior = posicaoAnterior;
        }

        public Integer getPosicaoAtual() {
            return posicaoAtual;
        }

        public void setPosicaoAtual(Integer posicaoAtual) {
            this.posicaoAtual = posicaoAtual;
        }

        public Integer getPontuacaoAnterior() {
            return pontuacaoAnterior;
        }

        public void setPontuacaoAnterior(Integer pontuacaoAnterior) {
            this.pontuacaoAnterior = pontuacaoAnterior;
        }

        public Integer getPontuacaoAtual() {
            return pontuacaoAtual;
        }

        public void setPontuacaoAtual(Integer pontuacaoAtual) {
            this.pontuacaoAtual = pontuacaoAtual;
        }

        public Integer getVariacao() {
            return variacao;
        }

        public void setVariacao(Integer variacao) {
            this.variacao = variacao;
        }

        public Integer getQuantidadePalpites() {
            return quantidadePalpites;
        }

        public void setQuantidadePalpites(Integer quantidadePalpites) {
            this.quantidadePalpites = quantidadePalpites;
        }

    }

    public class UsuarioRankingBolao implements Serializable {

        private Usuario usuario;
        private Integer pontuacao;
        private Integer quantidadePalpites;
        private Integer posicao;

        public UsuarioRankingBolao(Usuario usuario, Integer pontuacao, Integer quantidadePalpites, Integer posicao) {
            this.usuario = usuario;
            this.pontuacao = pontuacao;
            this.quantidadePalpites = quantidadePalpites;
            this.posicao = posicao;
        }

        public Usuario getUsuario() {
            return usuario;
        }

        public void setUsuario(Usuario usuario) {
            this.usuario = usuario;
        }

        public Integer getPontuacao() {
            return pontuacao;
        }

        public void setPontuacao(Integer pontuacao) {
            this.pontuacao = pontuacao;
        }

        public Integer getQuantidadePalpites() {
            return quantidadePalpites;
        }

        public void setQuantidadePalpites(Integer quantidadePalpites) {
            this.quantidadePalpites = quantidadePalpites;
        }

        public Integer getPosicao() {
            return posicao;
        }

        public void setPosicao(Integer posicao) {
            this.posicao = posicao;
        }
    }

}
