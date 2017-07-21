/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.bolao.dao;

import br.com.bolao.factory.ConnectionDB;
import br.com.bolao.model.Bolao;
import br.com.bolao.model.GrupoUsuario;
import br.com.bolao.model.Jogo;
import br.com.bolao.model.Palpite;
import br.com.bolao.model.Pontuacao;
import br.com.bolao.model.Ranking;
import br.com.bolao.model.Time;
import br.com.bolao.utils.FuncString;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class BolaoDao extends ConnectionDB {

    public List<Bolao> listaBolao(Integer id_grupo) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT b.* FROM bolao b WHERE b.id_grupo = " + id_grupo + " AND b.finalizado = false AND b.ativo = true ORDER BY b.id", Bolao.class
            );

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Jogo> listaJogo(Integer id_bolao) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "  SELECT j.* \n "
                    + "  FROM jogo j \n "
                    + " WHERE j.id_bolao = " + id_bolao + " \n "
                    + " ORDER BY j.data_jogo, j.hora_jogo", Jogo.class
            );

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Jogo> listaJogoAberto(Integer id_bolao) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "  SELECT j.* \n "
                    + "  FROM jogo j \n "
                    + " WHERE j.id_bolao = " + id_bolao + " \n "
                    + "   AND j.data_jogo_final IS NULL \n "
                    + "   AND j.hora_jogo_final = '' \n "
                    + " ORDER BY j.data_jogo, j.hora_jogo", Jogo.class
            );

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Bolao> listaBolaoGrupo(Integer id_grupo) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "  SELECT b.* \n"
                    + "  FROM bolao b \n"
                    + " WHERE b.ativo = true \n"
                    + "   AND b.id_grupo = " + id_grupo + " \n "
                    + " ORDER BY b.finalizado, b.id \n", Bolao.class
            );

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public Time pesquisaTime(String nome_time) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT t.* FROM time t WHERE LOWER(t.nome) LIKE '" + nome_time.toLowerCase() + "'", Time.class
            );

            return (Time) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public List<GrupoUsuario> listaGrupoUsuario(Integer id_usuario) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT gu.* FROM grupo_usuario gu WHERE gu.id_usuario = " + id_usuario, GrupoUsuario.class
            );

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<GrupoUsuario> listaGrupoUsuarioPorGrupo(Integer id_grupo) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT gu.* FROM grupo_usuario gu WHERE gu.id_grupo = " + id_grupo, GrupoUsuario.class
            );

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public Palpite pesquisaPalpite(Integer id_jogo, Integer id_usuario) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT p.* FROM palpite p WHERE p.id_jogo = " + id_jogo + " AND p.id_usuario = " + id_usuario, Palpite.class
            );

            return (Palpite) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public Palpite pesquisaPalpiteID(Integer id_palpite) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT p.* FROM palpite p WHERE p.id = " + id_palpite, Palpite.class
            );

            return (Palpite) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public Jogo pesquisaJogoID(Integer id_jogo) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT j.* FROM jogo j WHERE j.id = " + id_jogo, Jogo.class
            );

            return (Jogo) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public Integer pontuacaoGeral(Integer id_grupo, Integer id_usuario) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT func_pontuacao_geral(" + id_grupo + ", " + id_usuario + ") AS pontuacao_geral"
            );
            return (Integer) ((List) qry.getResultList()).get(0);
        } catch (Exception e) {
            e.getMessage();
        }
        return 0;
    }

    public List<Object[]> listaUsuarioRankingGeralGrupo(Integer id_grupo) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT u.id, \n"
                    + "     COALESCE(r.posicao, 0) AS posicao_anterior, \n"
                    + "     COALESCE(r.pontos, 0) AS pontos_anterior, \n"
                    + "     func_pontos_grupo_usuario(" + id_grupo + ", u.id) AS pontuacao_grupo,\n"
                    + "     COALESCE(r.posicao_anterior - r.posicao_atual, 0) AS variacao, \n"
                    + "     COALESCE(COUNT(p.id), 0)::integer AS quantidade_palpites \n"
                    + "  FROM usuario u\n"
                    + "  LEFT JOIN ranking r ON r.id_usuario = u.id AND r.id = (SELECT max(rx.id) FROM ranking rx INNER JOIN bolao b ON b.id = rx.id_bolao INNER JOIN grupo g ON g.id = b.id_grupo WHERE g.id = " + id_grupo + " AND rx.id_usuario = u.idSELECT max(rx.id) FROM ranking rx INNER JOIN bolao b ON b.id = rx.id_bolao INNER JOIN grupo g ON g.id = b.id_grupo WHERE g.id = " + id_grupo + " AND rx.id_usuario = u.id)\n"
                    + " INNER JOIN grupo_usuario gu ON gu.id_usuario = u.id \n"
                    + "  LEFT JOIN palpite p ON p.id_usuario = u.id \n"
                    + " WHERE gu.id_grupo = " + id_grupo + "\n"
                    + " GROUP BY u.id, posicao_anterior, pontos_anterior"
                    + " ORDER BY pontuacao_grupo DESC"
            );
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Object[]> listaUsuarioRankingGeralGrupoQuery(Integer id_grupo) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT u.id, \n"
                    + "     COALESCE(r.posicao, 0) AS posicao_anterior, \n"
                    + "     COALESCE(r.pontos, 0) AS pontos_anterior, \n"
                    + "     func_pontos_grupo_usuario(" + id_grupo + ", u.id) AS pontuacao_grupo,\n"
                    + "     COALESCE(r.posicao - rank() over (order by func_pontos_grupo_usuario(" + id_grupo + ", u.id) DESC, func_quantidade_palpites_grupo_usuario(" + id_grupo + ", u.id) DESC)::integer, 0) AS variacao, \n"
                    + "     func_quantidade_palpites_grupo_usuario(" + id_grupo + ", u.id) AS quantidade_palpites, \n"
                    + "     rank() over (order by func_pontos_grupo_usuario(" + id_grupo + ", u.id) DESC, func_quantidade_palpites_grupo_usuario(" + id_grupo + ", u.id) DESC)::integer as rank \n"
                    + "  FROM usuario u\n"
                    + "  LEFT JOIN ranking r ON r.id_usuario = u.id AND r.id = (SELECT max(rx.id) FROM ranking rx INNER JOIN bolao b ON b.id = rx.id_bolao INNER JOIN grupo g ON g.id = b.id_grupo WHERE g.id = " + id_grupo + " AND rx.id_usuario = u.id)\n"
                    + " INNER JOIN grupo_usuario gu ON gu.id_usuario = u.id \n"
                    + " WHERE gu.id_grupo = " + id_grupo + "\n"
                    + " GROUP BY u.id, r.posicao, pontos_anterior ORDER BY pontuacao_grupo DESC, quantidade_palpites DESC, u.nome"
            );
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Object[]> listaUsuarioRankingBolao(Integer id_bolao) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT u.id, \n"
                    + "       func_pontos_bolao_usuario(" + id_bolao + ", u.id) AS pontuacao_bolao,\n"
                    + "       func_quantidade_palpites_bolao_usuario(" + id_bolao + ", u.id) AS quantidade_palpites, \n"
                    + "       rank() over (ORDER BY func_pontos_bolao_usuario(" + id_bolao + ", u.id) DESC, func_quantidade_palpites_bolao_usuario(" + id_bolao + ", u.id) DESC)::integer as rank \n"
                    + "  FROM usuario u \n"
                    + " INNER JOIN grupo_usuario gu ON gu.id_usuario = u.id \n "
                    + " INNER JOIN bolao b ON b.id_grupo = gu.id_grupo \n "
                    + " WHERE b.id = " + id_bolao + " \n "
                    + " GROUP BY u.id  ORDER BY pontuacao_bolao DESC, quantidade_palpites DESC, u.nome"
            );
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public Jogo pesquisaJogoDisponivelPalpite(Integer id_jogo, String data, String hora) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT j.id \n "
                    + "  FROM jogo j \n "
                    + " WHERE (j.data_jogo = '" + data + "' AND j.hora_jogo >= '" + hora + "' OR j.data_jogo > '" + data + "') \n"
                    + "   AND j.id = " + id_jogo, Jogo.class
            );
            return (Jogo) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public List<Palpite> listaPalpite(Integer id_jogo) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT p.* \n "
                    + "  FROM palpite p \n "
                    + " WHERE p.id_jogo = " + id_jogo, Palpite.class
            );
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public Integer pontuacaoPalpite(Integer id_palpite) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT func_pontos_palpite(" + id_palpite + ") AS pontos_palpite"
            );
            return (Integer) ((List) qry.getResultList()).get(0);
        } catch (Exception e) {
            e.getMessage();
        }
        return 0;
    }

    public Object[] pesquisaUltimoRankingDoBolao(Integer id_bolao) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT MAX(r.posicao) \n"
                    + "  FROM ranking r \n"
                    + " WHERE r.id_bolao = " + id_bolao
            );
            return (Object[]) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public List<Ranking> listaRankingBolao(Integer id_bolao, Integer id_usuario) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT r.* \n "
                    + "  FROM ranking r \n "
                    + " WHERE r.id_bolao = " + id_bolao + " \n "
                    + "   AND r.id_usuario = " + id_usuario, Ranking.class
            );
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Pontuacao> listaPontuacao() {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "  SELECT p.* \n "
                    + "  FROM pontuacao p \n "
                    + " ORDER BY p.id", Pontuacao.class
            );
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Time> listaPesquisaTime(String nome) {
        try {
            nome = FuncString.normalizeLower(nome);

            String QUERY 
                    = "SELECT t.* \n"
                    + "  FROM time t \n"
                    + " WHERE TRANSLATE(LOWER(t.nome)) LIKE '%" + nome + "%'";

            Integer LIMIT;

            switch (nome.length()) {
                case 1:
                case 2:
                    LIMIT = 5;
                    break;
                default:
                    LIMIT = 8;
                    break;
            }

            Query qry = getEntityManager().createNativeQuery(QUERY + " ORDER BY nome \n LIMIT " + LIMIT, Time.class);
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

}
/*


-- ACERTOU QUEM GANHOU
SELECT CASE 
       WHEN (p.placar_time1 > p.placar_time2 AND j.placar_time1 > j.placar_time2) OR
            (p.placar_time1 < p.placar_time2 AND j.placar_time1 < j.placar_time2)
       THEN (SELECT po.pontos FROM pontuacao po WHERE po.id = 1)
       ELSE 0
       END AS pontos
  FROM palpite p
 INNER JOIN bolao_jogo bj ON bj.id = p.id_bolao_jogo
 INNER JOIN jogo j ON j.id = bj.id_jogo
 INNER JOIN bolao b ON b.id = bj.id_bolao
 WHERE p.id = 1

-- ACERTOU O PLACAR
SELECT CASE 
       WHEN (p.placar_time1 = j.placar_time1 AND p.placar_time2 = j.placar_time2) THEN (SELECT po.pontos FROM pontuacao po WHERE po.id = 2)
       ELSE 0
       END
  FROM palpite p
 INNER JOIN bolao_jogo bj ON bj.id = p.id_bolao_jogo
 INNER JOIN jogo j ON j.id = bj.id_jogo
 WHERE p.id = 1


-- ERROU QUEM GANHOU
SELECT CASE 
       WHEN (p.placar_time1 > j.placar_time1 AND p.placar_time2 < j.placar_time2) OR
            (p.placar_time1 < j.placar_time1 AND p.placar_time2 > j.placar_time2)
       THEN (SELECT po.pontos FROM pontuacao po WHERE po.id = 3)
       ELSE 0
       END
  FROM palpite p
 INNER JOIN bolao_jogo bj ON bj.id = p.id_bolao_jogo
 INNER JOIN jogo j ON j.id = bj.id_jogo
 WHERE p.id = 1



-- ACERTOU O EMPATE
SELECT CASE 
       WHEN (p.placar_time1 = p.placar_time2 AND j.placar_time1 = j.placar_time2) THEN (SELECT po.pontos FROM pontuacao po WHERE po.id = 4)
       ELSE 0
       END
  FROM palpite p
 INNER JOIN bolao_jogo bj ON bj.id = p.id_bolao_jogo
 INNER JOIN jogo j ON j.id = bj.id_jogo
 WHERE p.id = 1

 */
