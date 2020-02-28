/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

/**
 *
 * @author Lara Vazquez Dorna
 */
public class Usuario {

    
    private String nick;
    

    public Usuario() {
    }

    public Usuario(String nick, String password) {
        this.nick = nick;
      
    }


    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }


}
