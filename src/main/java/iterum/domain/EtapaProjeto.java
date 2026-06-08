/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package iterum.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author evandro
 */
@Entity
@Table(name = "etapas_projeto")
public class EtapaProjeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private boolean etapaDeConclucao;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(nullable = false, length = 20)
    private String hexColor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "projeto_id", nullable = false)
    private Projeto projeto;

    @OneToMany(mappedBy = "etapa", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("id ASC")
    private List<Tarefa> tarefas = new ArrayList<>();

    protected EtapaProjeto() {
    }

    public EtapaProjeto(String nome, String hexColor, boolean etapaDeConclucao) {
        this.nome = nome;
        this.hexColor = hexColor;
        this.etapaDeConclucao = etapaDeConclucao;
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
        vincularTarefas();
    }

    public boolean isEtapaDeConclucao() {
        return etapaDeConclucao;
    }

    public void setEtapaDeConclucao(boolean etapaDeConclucao) {
        this.etapaDeConclucao = etapaDeConclucao;
    }

    public Projeto getProjeto() {
        return projeto;
    }

    public void setProjeto(Projeto projeto) {
        this.projeto = projeto;
    }

    private void vincularTarefas() {
        if (tarefas == null) {
            return;
        }
        for (Tarefa tarefa : tarefas) {
            tarefa.setEtapa(this);
        }
    }

}
