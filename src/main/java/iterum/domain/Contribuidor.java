/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package iterum.domain;

/**
 *
 * @author evandro
 */
public class Contribuidor {
    private int id;
    private String nome;
    private String email;

    public Contribuidor(int id, String nome) {
        this(id, nome, "");
    }

    public Contribuidor(int id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    public Contribuidor(String nome) {
        this(0, nome, "");
    }

    public Contribuidor(String nome, String email) {
        this(0, nome, email);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
