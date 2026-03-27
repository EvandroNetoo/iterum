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
public class Projeto {

    private int id;
    private String nome;

    private List<EtapaProjeto> etapas;

    public Projeto(int id, String nome) {
        this.id = id;
        this.nome = nome;
        this.etapas = new ArrayList<>(Arrays.asList(
                new EtapaProjeto(1, "BACKLOG", "#8E8E93", false),
                new EtapaProjeto(2, "EM ANDAMENTO", "#0A84FF", false),
                new EtapaProjeto(3, "REVISAO", "#FF9F0A", false),
                new EtapaProjeto(4, "CONCLUIDO", "#30D158", true)));
    }

    public Projeto(String nome) {
        this(0, nome);
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

    public List<EtapaProjeto> getEtapas() {
        return etapas;
    }

    public void setEtapas(List<EtapaProjeto> etapas) {
        this.etapas = etapas;
    }

}
