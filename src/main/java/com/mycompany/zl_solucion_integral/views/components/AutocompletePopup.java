package com.mycompany.zl_solucion_integral.views.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Componente genérico y reutilizable de autocompletado y sugerencias para JTextFields (DRY).
 *
 * @param <T> Tipo de objeto a buscar y autocompletar (ej: Producto, Usuario, etc.)
 */
public class AutocompletePopup<T> {

    @FunctionalInterface
    public interface SearchProvider<T> {
        List<T> search(String query);
    }

    @FunctionalInterface
    public interface DisplayFormatter<T> {
        String format(T item);
    }

    @FunctionalInterface
    public interface SelectionListener<T> {
        void onSelect(T item);
    }

    private final JTextField textField;
    private final SearchProvider<T> provider;
    private final DisplayFormatter<T> formatter;
    private final SelectionListener<T> listener;

    private final JPopupMenu popup = new JPopupMenu();
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> list = new JList<>(listModel);
    private List<T> currentItems = new ArrayList<>();
    private boolean isAdjusting = false;

    public AutocompletePopup(JTextField textField, SearchProvider<T> provider, DisplayFormatter<T> formatter, SelectionListener<T> listener) {
        this.textField = textField;
        this.provider = provider;
        this.formatter = formatter;
        this.listener = listener;

        initUI();
        initListeners();
    }

    public static <T> AutocompletePopup<T> attach(JTextField textField, SearchProvider<T> provider, DisplayFormatter<T> formatter, SelectionListener<T> listener) {
        return new AutocompletePopup<>(textField, provider, formatter, listener);
    }

    private void initUI() {
        popup.setFocusable(false);
        list.setFont(ThemeConstants.FONT_SMALL);
        list.setBackground(ThemeConstants.SIDEBAR_BACKGROUND);
        list.setForeground(ThemeConstants.TEXT_PRIMARY);
        list.setSelectionBackground(new Color(6, 182, 212, 70));
        list.setSelectionForeground(ThemeConstants.NEON_CYAN);
        list.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setPreferredSize(new Dimension(320, 160));
        popup.add(scroll);
    }

    private void initListeners() {
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onDocumentChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onDocumentChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onDocumentChange();
            }
        });

        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 1) {
                    selectItem();
                }
            }
        });

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (popup.isVisible()) {
                    if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        int index = list.getSelectedIndex();
                        if (index < listModel.getSize() - 1) {
                            list.setSelectedIndex(index + 1);
                            list.ensureIndexIsVisible(index + 1);
                        }
                        e.consume();
                    } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                        int index = list.getSelectedIndex();
                        if (index > 0) {
                            list.setSelectedIndex(index - 1);
                            list.ensureIndexIsVisible(index - 1);
                        }
                        e.consume();
                    } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        if (list.getSelectedIndex() != -1) {
                            selectItem();
                            e.consume();
                        }
                    } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        popup.setVisible(false);
                    }
                }
            }
        });
    }

    private void onDocumentChange() {
        if (isAdjusting) return;
        String query = textField.getText().trim();
        if (query.length() < 1) {
            popup.setVisible(false);
            currentItems.clear();
            return;
        }

        currentItems = provider.search(query);
        listModel.clear();

        if (currentItems == null || currentItems.isEmpty()) {
            popup.setVisible(false);
        } else {
            for (T item : currentItems) {
                listModel.addElement(formatter.format(item));
            }
            list.setSelectedIndex(0);
            popup.setPopupSize(new Dimension(Math.max(textField.getWidth(), 250), 180));
            popup.show(textField, 0, textField.getHeight());
            textField.requestFocusInWindow();
        }
    }

    private void selectItem() {
        int selectedIndex = list.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < currentItems.size()) {
            T selected = currentItems.get(selectedIndex);
            isAdjusting = true;
            popup.setVisible(false);
            listener.onSelect(selected);
            isAdjusting = false;
        }
    }

    public void hidePopup() {
        popup.setVisible(false);
    }
}
