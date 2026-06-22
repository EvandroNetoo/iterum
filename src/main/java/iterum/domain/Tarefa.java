/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package iterum.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 *
 * @author evandro
 */
@Entity
@Table(name = "tarefas")
public class Tarefa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 200)
    private String nome;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "etapa_id", nullable = false)
    private EtapaProjeto etapa;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tarefa_contribuidor", joinColumns = @JoinColumn(name = "tarefa_id"), inverseJoinColumns = @JoinColumn(name = "contribuidor_id"))
    @Fetch(FetchMode.SUBSELECT)
    private List<Contribuidor> contribuidores = new ArrayList<>();

    protected Tarefa() {
    }

    public Tarefa(String nome) {
        this.nome = nome;
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

    public List<Contribuidor> getContribuidores() {
        return contribuidores;
    }

    public void setContribuidores(List<Contribuidor> contribuidores) {
        this.contribuidores = contribuidores;
        vincularContribuidores();
    }

    public EtapaProjeto getEtapa() {
        return etapa;
    }

    public void setEtapa(EtapaProjeto etapa) {
        this.etapa = etapa;
    }

    public void addContribuidor(Contribuidor contribuidor) {
        if (contribuidor != null && !contribuidores.contains(contribuidor)) {
            contribuidores.add(contribuidor);
            contribuidor.getTarefas().add(this);
        }
    }

    public void removerContribuidor(Contribuidor contribuidor) {
        if (contribuidor == null) {
            return;
        }
        contribuidores.remove(contribuidor);
        contribuidor.getTarefas().remove(this);
    }

    public void limparContribuidores() {
        for (Contribuidor contribuidor : new ArrayList<>(contribuidores)) {
            removerContribuidor(contribuidor);
        }
    }

    private void vincularContribuidores() {
        if (contribuidores == null) {
            return;
        }
        for (Contribuidor contribuidor : contribuidores) {
            if (!contribuidor.getTarefas().contains(this)) {
                contribuidor.getTarefas().add(this);
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Tarefa other)) {
            return false;
        }
        return id != null && Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return id == null ? System.identityHashCode(this) : Objects.hash(id);
    }

}
