/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package iterum.domain;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author evandro
 */
public class Tarefa {
    private int id;
    private String nome;

    private List<Contribuidor> contribuidores;

    public Tarefa(int id, String nome) {
        this.id = id;
        this.nome = nome;

        contribuidores = new ArrayList<>(Arrays.asList(
                new Contribuidor(1, "Evandro Neto"),
                new Contribuidor(2, "Gabriel Rocha")));
    }

    public Tarefa(String nome) {
        this.nome = nome;
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

    public List<Contribuidor> getContribuidores() {
        return contribuidores;
    }

    public void setContribuidores(List<Contribuidor> contribuidores) {
        this.contribuidores = contribuidores;
    }

}
