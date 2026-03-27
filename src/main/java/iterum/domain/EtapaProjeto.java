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
public class EtapaProjeto {

    private int id;
    private boolean etapaDeConclucao;
    private String nome;
    private String hexColor;

    private List<Tarefa> tarefas;

    public EtapaProjeto(int id, String nome, String hexColor, boolean etapaDeConclucao) {
        this.id = id;
        this.nome = nome;
        this.hexColor = hexColor;
        this.etapaDeConclucao = etapaDeConclucao;

        this.tarefas = new ArrayList<>(Arrays.asList(
                new Tarefa(1, "Fazer o front"),
                new Tarefa(2, "Fazer o back")));
    }

    public EtapaProjeto(String nome, String hexColor, boolean etapaDeConclucao) {
        this(0, nome, hexColor, etapaDeConclucao);
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

    public String getHexColor() {
        return hexColor;
    }

    public void setHexColor(String hexColor) {
        this.hexColor = hexColor;
    }

    public List<Tarefa> getTarefas() {
        return tarefas;
    }

    public void setTarefas(List<Tarefa> tarefas) {
        this.tarefas = tarefas;
    }

    public boolean isEtapaDeConclucao() {
        return etapaDeConclucao;
    }

    public void setEtapaDeConclucao(boolean etapaDeConclucao) {
        this.etapaDeConclucao = etapaDeConclucao;
    }

}
