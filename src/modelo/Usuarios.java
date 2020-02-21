/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dam2
 */
public class Usuarios {
    
    
    
    List<Usuario> listado = new ArrayList<>();
    
    
    //ESTE METODO ES DE LA BASE DE DATOS COMO LO CAMBIO PARA HACER UNA LISTA DIRECTAMENTE DE USUARIOS
    
     public List<Usuario> getUsuarios() throws SQLException, ClassNotFoundException, Exception {
        List<Usuario> listado = new ArrayList<>();
        Connection con;
        Statement stm;
        ResultSet rs;
        String sql;

        sql = "SELECT * FROM usuarios";

        if (acceso == null) {
            return null;
        } else {
            con = acceso.getConexion();
        }

        stm = con.createStatement();
        rs = stm.executeQuery(sql);
        while (rs.next()) {
            Usuario u = new Usuario();

            u.setId(rs.getInt("id"));
            u.setNick(rs.getString(("nick")));
            u.setPassword(rs.getString("password"));
            listado.add(u);
        }
        rs.close();
        stm.close();
        con.close();
        return listado;
    }
}
