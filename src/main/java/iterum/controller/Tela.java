/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package iterum.controller;

import iterum.viewer.PnlDashboard;
import iterum.viewer.PnlEquipe;
import iterum.viewer.PnlProjeto;
import iterum.viewer.PnlProjetos;
import iterum.viewer.PnlRelatorios;
import javax.swing.JPanel;


public enum Tela {
    DASHBOARD("dashboard", "DASHBOARD", () -> new PnlDashboard()),
    
    PROJETOS("projetos", "PROJETOS", () -> new PnlProjetos()),
    PROJETO("projeto", "", () -> new PnlProjeto()),
    
    EQUIPE("equipe", "EQUIPE", () -> new PnlEquipe()),
    RELATORIOS("relatorios", "RELATÓRIOS", () -> new PnlRelatorios());

    private final String nome;
    private final String label;
    private final java.util.function.Supplier<JPanel> lambdaPanel;
    private JPanel panel;

    private Tela(String nome, String label, java.util.function.Supplier<JPanel> lambdaPanel) {
        this.nome = nome;
        this.label = label;
        this.lambdaPanel = lambdaPanel;
        this.panel = null;
    }

    public String getNome() {
        return nome;
    }

    public String getLabel() {
        return this.label;
    }

    public JPanel getPanel() {
        if (panel == null) {
            this.panel = lambdaPanel.get();
        }
        return this.panel;
    }
}
