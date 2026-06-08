package iterum.controller;

import iterum.domain.Contribuidor;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class TableModelContribuidor extends AbstractTableModel {

    private List<Contribuidor> lista = new ArrayList<>();

    @Override
    public int getRowCount() {
        return lista.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Contribuidor contribuidor = lista.get(rowIndex);

        return switch (columnIndex) {
            case 0 -> contribuidor.getNome();
            case 1 -> contribuidor.getEmail();
            default -> null;
        };
    }

    @Override
    public String getColumnName(int column) {
        String[] nomes = {"Nome", "Email"};
        return nomes[column];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public Contribuidor getItem(int rowIndex) {
        return lista.get(rowIndex);
    }

    public void adicionar(Contribuidor contribuidor) {
        lista.add(contribuidor);
        fireTableRowsInserted(lista.size() - 1, lista.size() - 1);
    }

    public void setLista(List<Contribuidor> novaLista) {
        lista = novaLista == null ? new ArrayList<>() : new ArrayList<>(novaLista);
        fireTableDataChanged();
    }

    public List<Contribuidor> getLista() {
        return lista;
    }
}
