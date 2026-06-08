/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package iterum.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author evandro
 */
@Entity
@Table(name = "projetos")
public class Projeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 200)
    private String nome;

    @OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderColumn(name = "ordem")
    private List<EtapaProjeto> etapas = new ArrayList<>();

    protected Projeto() {
    }

    public Projeto(String nome) {
        this.nome = nome;
        this.etapas = new ArrayList<>(Arrays.asList(
                new EtapaProjeto("BACKLOG", "#8E8E93", false),
                new EtapaProjeto("EM ANDAMENTO", "#0A84FF", false),
                new EtapaProjeto("REVISAO", "#FF9F0A", false),
                new EtapaProjeto("CONCLUIDO", "#30D158", true)));
        vincularEtapas();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
        vincularEtapas();
    }

    private void vincularEtapas() {
        if (etapas == null) {
            return;
        }
        for (EtapaProjeto etapa : etapas) {
            etapa.setProjeto(this);
        }
    }

}
