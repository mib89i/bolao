/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.bolao.dao;

import br.com.bolao.factory.ConnectionDB;
import br.com.bolao.model.GrupoUsuario;
import br.com.bolao.model.Usuario;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;

/**
 *
 * @author Claudemir Rtools
 */
public class UsuarioDao extends ConnectionDB {

    public Usuario pesquisaUsuario(String usuario, String senha) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT u.* FROM usuario u WHERE lower(u.email) = '" + usuario + "' AND u.senha = '" + senha + "'", Usuario.class
            );

            return (Usuario) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public Usuario pesquisaUsuarioLoginExiste(Integer id_usuario, String email) {
        String text
                = "SELECT u.* \n"
                + "  FROM usuario u \n"
                + (id_usuario != null ? " WHERE u.id <> " + id_usuario + " AND lower(u.email) = '" + email + "'" : " WHERE u.email = '" + email + "'");
        try {
            Query qry = getEntityManager().createNativeQuery(
                    text, Usuario.class
            );

            return (Usuario) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public List<Usuario> listaUsuarioGrupo(Integer id_grupo) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT u.* \n"
                    + "  FROM usuario u \n"
                    + " INNER JOIN grupo_usuario gu ON gu.id_usuario = u.id \n"
                    + " WHERE gu.id_grupo = " + id_grupo, Usuario.class
            );

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public List<Usuario> listaUsuarioQueNaoEstaNoGrupo(Integer id_grupo) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT u.* \n"
                    + "  FROM usuario u\n"
                    + " WHERE u.id NOT IN (SELECT gu.id_usuario FROM grupo_usuario gu WHERE gu.id_grupo = " + id_grupo + ")", Usuario.class
            );

            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }

    public GrupoUsuario pesquisaGrupoUsuario(Integer id_grupo, Integer id_usuario) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "  SELECT gu.* \n"
                    + "  FROM grupo_usuario gu \n"
                    + " WHERE gu.id_grupo = " + id_grupo
                    + "   AND gu.id_usuario = " + id_usuario, GrupoUsuario.class
            );

            return (GrupoUsuario) qry.getSingleResult();
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public List<Object[]> listaUsuarioMenosSessao(Integer id_usuario, Integer id_grupo) {
        try {
            Query qry = getEntityManager().createNativeQuery(
                    "SELECT u.id, \n "
                    + "     u.nome, \n "
                    + "     CASE WHEN u.id IN (SELECT gu.id_usuario FROM grupo_usuario gu WHERE gu.id_grupo = " + id_grupo + ") THEN 'remover' ELSE 'adicionar' END \n "
                    + " FROM usuario u \n "
                    + "WHERE u.id <> " + id_usuario
            );
            return qry.getResultList();
        } catch (Exception e) {
            e.getMessage();
        }
        return new ArrayList();
    }
}
