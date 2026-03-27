/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package iterum.domain;

import java.util.Arrays;
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
        this.etapas = Arrays.asList(
                new EtapaProjeto(1, "BACKLOCK", "#ffffff", false),
                new EtapaProjeto(2, "BACKLOCK", "#999999", false),
                new EtapaProjeto(3, "BACKLOCK", "#333333", true)
        );
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

}
