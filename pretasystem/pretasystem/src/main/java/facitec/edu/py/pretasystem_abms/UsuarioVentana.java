
package facitec.edu.py.pretasystem_abms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import facitec.edu.py.pretasystem_dao.Usuario_Dao;
import facitec.edu.py.pretasystem_entidades.Usuario;
import facitec.edu.py.pretasystem_util.SeguridadUtil;
import facitec.edu.py.pretasystem_util.SesionUsuario;

public class UsuarioVentana extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final Color ROSA_FUERTE = new Color(236, 64, 122);
    private static final Color ROSA_OSCURO = new Color(173, 20, 87);
    private static final Color ROSA_MEDIO = new Color(244, 143, 177);
    private static final Color ROSA_CLARO = new Color(252, 228, 236);
    private static final Color TEXTO = new Color(75, 55, 65);
    private static final Color FONDO_CAMPO = new Color(255, 250, 252);

    private static final String PUNTOS = "********";

    private JTextField txtUsuario;
    private JTextField txtCorreo;
    private JPasswordField txtClave;
    private JPasswordField txtClave2;

    private JTable table;
    private DefaultTableModel modelo;

    private final Usuario_Dao dao = new Usuario_Dao();
    private Usuario seleccionado = null;

    private JButton btnGuardar;
    private JButton btnEliminar;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new UsuarioVentana().setVisible(true));
    }

    public UsuarioVentana() {

        setTitle("ABM - Usuarios");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setSize(1100, 680);
        setResizable(false);
        setLocationRelativeTo(null);

        boolean esAdmin = puedeAdministrar();

        if (SesionUsuario.esAdministrador()) {
            setTitle("ABM - Usuarios (Administrador)");
        }

        crearInterfaz(esAdmin);
        cargarTabla();
    }

    private void crearInterfaz(boolean esAdmin) {

        FondoPanel fondo = new FondoPanel();
        fondo.setLayout(new BorderLayout(0, 18));
        fondo.setBorder(new EmptyBorder(22, 25, 25, 25));

        setContentPane(fondo);

        // ENCABEZADO
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 5));
        textos.setOpaque(false);

        JLabel titulo = new JLabel("Gestión de Usuarios");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 27));
        titulo.setForeground(ROSA_OSCURO);

        JLabel subtitulo = new JLabel(
                "Administración de cuentas, datos de acceso y contraseñas");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(TEXTO);

        textos.add(titulo);
        textos.add(subtitulo);

        JButton btnSalir = crearBotonSecundario("Salir");
        btnSalir.addActionListener(e -> dispose());

        encabezado.add(textos, BorderLayout.WEST);
        encabezado.add(btnSalir, BorderLayout.EAST);

        fondo.add(encabezado, BorderLayout.NORTH);

        // TARJETA PRINCIPAL
        PanelTarjeta tarjeta = new PanelTarjeta();
        tarjeta.setLayout(new BorderLayout(20, 0));
        tarjeta.setBorder(new EmptyBorder(22, 22, 22, 22));

        fondo.add(tarjeta, BorderLayout.CENTER);

        // FORMULARIO
        JPanel formulario = crearFormulario(esAdmin);
        formulario.setPreferredSize(new Dimension(330, 0));

        tarjeta.add(formulario, BorderLayout.WEST);

        // SEPARADOR
        JSeparator separador = new JSeparator(SwingConstants.VERTICAL);
        separador.setForeground(ROSA_CLARO);
        tarjeta.add(separador, BorderLayout.CENTER);

        // TABLA
        JPanel listado = crearListado();
        tarjeta.add(listado, BorderLayout.EAST);

        listado.setPreferredSize(new Dimension(660, 0));
    }

    private JPanel crearFormulario(boolean esAdmin) {

        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);

        JLabel titulo = new JLabel("Datos del usuario");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(ROSA_OSCURO);

        panel.add(titulo, BorderLayout.NORTH);

        JPanel campos = new JPanel(new GridLayout(4, 1, 0, 12));
        campos.setOpaque(false);

        txtUsuario = crearCampoTexto();
        txtCorreo = crearCampoTexto();
        txtClave = crearCampoClave();
        txtClave2 = crearCampoClave();

        campos.add(crearGrupoCampo("Nombre de usuario", txtUsuario));
        campos.add(crearGrupoCampo("Correo electrónico", txtCorreo));
        campos.add(crearGrupoCampo("Contraseña", txtClave));
        campos.add(crearGrupoCampo("Repetir contraseña", txtClave2));

        activarLimpiezaDePuntos();

        JPanel centro = new JPanel(new BorderLayout(0, 12));
        centro.setOpaque(false);

        centro.add(campos, BorderLayout.NORTH);

        JLabel nota = new JLabel(
                "<html><div style='width:260px;'>"
                + "Al editar, dejá los asteriscos para mantener "
                + "la contraseña actual o escribí una nueva."
                + "</div></html>");

        nota.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        nota.setForeground(new Color(110, 90, 100));

        if (!esAdmin) {
            nota.setText(
                    "<html><div style='width:260px;'>"
                    + "Solo el administrador puede registrar, "
                    + "editar o eliminar usuarios."
                    + "</div></html>");
        }

        centro.add(nota, BorderLayout.CENTER);

        panel.add(centro, BorderLayout.CENTER);

        // BOTONES
        JPanel botones = new JPanel(new GridLayout(2, 2, 10, 10));
        botones.setOpaque(false);

        btnGuardar = crearBotonRosa("Guardar");
        btnEliminar = crearBotonEliminar("Eliminar");

        JButton btnLimpiar = crearBotonSecundario("Limpiar");
        JButton btnSalir = crearBotonSecundario("Salir");

        btnGuardar.addActionListener(e -> guardar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
        btnSalir.addActionListener(e -> dispose());

        btnGuardar.setEnabled(esAdmin);
        btnEliminar.setEnabled(esAdmin);

        botones.add(btnGuardar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);
        botones.add(btnSalir);

        panel.add(botones, BorderLayout.SOUTH);

        txtUsuario.setEnabled(esAdmin);
        txtCorreo.setEnabled(esAdmin);
        txtClave.setEnabled(esAdmin);
        txtClave2.setEnabled(esAdmin);

        return panel;
    }

    private JPanel crearListado() {

        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        JLabel titulo = new JLabel("Usuarios registrados");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(ROSA_OSCURO);

        panel.add(titulo, BorderLayout.NORTH);

        modelo = new DefaultTableModel(
                new Object[]{"ID", "Usuario", "Correo"}, 0) {

            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(modelo);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(34);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(TEXTO);
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(ROSA_CLARO);
        table.setSelectionForeground(ROSA_OSCURO);
        table.setGridColor(new Color(245, 225, 234));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader encabezado = table.getTableHeader();
        encabezado.setFont(new Font("Segoe UI", Font.BOLD, 13));
        encabezado.setBackground(ROSA_FUERTE);
        encabezado.setForeground(Color.WHITE);
        encabezado.setPreferredSize(new Dimension(0, 38));
        encabezado.setReorderingAllowed(false);

        table.getColumnModel().getColumn(0).setPreferredWidth(55);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(350);

        table.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {

                int fila = table.getSelectedRow();

                int id = ((Number) modelo.getValueAt(fila, 0)).intValue();

                seleccionado = dao.recuperarPorId(id);

                if (seleccionado != null) {

                    txtUsuario.setText(seleccionado.getUsuario());
                    txtCorreo.setText(seleccionado.getCorreo());

                    // La contraseña cifrada no se muestra.
                    txtClave.setText(PUNTOS);
                    txtClave2.setText(PUNTOS);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(ROSA_CLARO));
        scroll.getViewport().setBackground(Color.WHITE);

        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearGrupoCampo(String etiqueta, JTextField campo) {

        JPanel grupo = new JPanel(new BorderLayout(0, 6));
        grupo.setOpaque(false);

        JLabel label = new JLabel(etiqueta);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(TEXTO);

        campo.setPreferredSize(new Dimension(280, 37));
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campo.setForeground(TEXTO);
        campo.setBackground(FONDO_CAMPO);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ROSA_MEDIO, 1),
                new EmptyBorder(5, 10, 5, 10)));

        grupo.add(label, BorderLayout.NORTH);
        grupo.add(campo, BorderLayout.CENTER);

        return grupo;
    }

    private JTextField crearCampoTexto() {
        return new JTextField();
    }

    private JPasswordField crearCampoClave() {
        return new JPasswordField();
    }

    private JButton crearBotonRosa(String texto) {

        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setForeground(Color.WHITE);
        boton.setBackground(ROSA_FUERTE);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(130, 38));

        return boton;
    }

    private JButton crearBotonSecundario(String texto) {

        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setForeground(ROSA_OSCURO);
        boton.setBackground(ROSA_CLARO);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createLineBorder(ROSA_MEDIO));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(100, 38));

        return boton;
    }

    private JButton crearBotonEliminar(String texto) {

        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setForeground(Color.WHITE);
        boton.setBackground(new Color(198, 40, 90));
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(130, 38));

        return boton;
    }

    // Al hacer clic en los asteriscos, se vacían ambos campos.
    private void activarLimpiezaDePuntos() {

        FocusAdapter fa = new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {

                if (new String(txtClave.getPassword()).equals(PUNTOS)) {
                    txtClave.setText("");
                    txtClave2.setText("");
                }
            }
        };

        txtClave.addFocusListener(fa);
        txtClave2.addFocusListener(fa);
    }

    // Es administrador o todavía no hay ningún usuario registrado.
    private boolean puedeAdministrar() {
        return SesionUsuario.esAdministrador() || dao.contarUsuarios() == 0;
    }

    private void cargarTabla() {

        List<Usuario> lista = dao.recuperarPorFiltro("");

        modelo.setRowCount(0);

        for (Usuario u : lista) {

            modelo.addRow(new Object[]{
                u.getId_usuario(),
                u.getUsuario(),
                u.getCorreo()
            });
        }
    }

    private void guardar() {

        String nombre = txtUsuario.getText().trim();
        String correo = txtCorreo.getText().trim();

        String c1 = new String(txtClave.getPassword());
        String c2 = new String(txtClave2.getPassword());

        // Si quedaron los asteriscos, no se modifica la contraseña.
        if (c1.equals(PUNTOS)) {
            c1 = "";
        }

        if (c2.equals(PUNTOS)) {
            c2 = "";
        }

        if (!puedeAdministrar()) {
            JOptionPane.showMessageDialog(this,
                    "Solo el administrador puede registrar o editar usuarios");
            return;
        }

        if (nombre.isEmpty() || correo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Usuario y correo son obligatorios");
            return;
        }

        if (!correo.contains("@")) {
            JOptionPane.showMessageDialog(this,
                    "El correo no es válido");
            return;
        }

        if (seleccionado == null && c1.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "La contraseña es obligatoria");
            return;
        }

        if (!c1.isEmpty()) {

            if (c1.length() < 4) {
                JOptionPane.showMessageDialog(this,
                        "La contraseña debe tener al menos 4 caracteres");
                return;
            }

            if (!c1.equals(c2)) {
                JOptionPane.showMessageDialog(this,
                        "Las contraseñas no coinciden");
                return;
            }
        }

        try {

            Usuario u = seleccionado == null
                    ? new Usuario()
                    : seleccionado;

            u.setUsuario(nombre);
            u.setCorreo(correo);

            if (!c1.isEmpty()) {
                u.setContrasena(SeguridadUtil.sha256(c1));
            }

            dao.guardar(u);

            JOptionPane.showMessageDialog(this,
                    "Guardado correctamente");

            limpiar();
            cargarTabla();

        } catch (Exception ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar. ¿Ya existe ese nombre de usuario?");
        }
    }

    private void eliminar() {

        if (!puedeAdministrar()) {
            JOptionPane.showMessageDialog(this,
                    "Solo el administrador puede eliminar usuarios");
            return;
        }

        if (seleccionado == null) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un usuario de la tabla");
            return;
        }

        Usuario actual = SesionUsuario.getUsuario();

        if (actual != null
                && actual.getId_usuario() == seleccionado.getId_usuario()) {

            JOptionPane.showMessageDialog(this,
                    "No podés eliminar el usuario con el que iniciaste sesión");
            return;
        }

        int op = JOptionPane.showConfirmDialog(
                this,
                "¿Eliminar \"" + seleccionado.getUsuario() + "\"?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (op != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            dao.eliminar(seleccionado);

            JOptionPane.showMessageDialog(this,
                    "Usuario eliminado correctamente");

            limpiar();
            cargarTabla();

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(this,
                    "No se pudo eliminar (puede tener órdenes de producción asociadas).");
        }
    }

    private void limpiar() {

        seleccionado = null;

        txtUsuario.setText("");
        txtCorreo.setText("");
        txtClave.setText("");
        txtClave2.setText("");

        table.clearSelection();
    }

    // FONDO DEGRADADO
    private static class FondoPanel extends JPanel {

        private static final long serialVersionUID = 1L;

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gradiente = new GradientPaint(
                    0, 0,
                    new Color(255, 245, 248),
                    getWidth(), getHeight(),
                    new Color(250, 205, 222));

            g2.setPaint(gradiente);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(new Color(236, 64, 122, 18));
            g2.fillOval(-70, -80, 250, 250);
            g2.fillOval(getWidth() - 180, getHeight() - 170, 260, 260);

            g2.dispose();
        }
    }

    // TARJETA BLANCA CON SOMBRA
    private static class PanelTarjeta extends JPanel {

        private static final long serialVersionUID = 1L;

        public PanelTarjeta() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(120, 50, 75, 25));
            g2.fillRoundRect(
                    5, 6,
                    getWidth() - 10,
                    getHeight() - 10,
                    25, 25);

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(
                    0, 0,
                    getWidth() - 10,
                    getHeight() - 10,
                    25, 25);

            g2.dispose();

            super.paintComponent(g);
        }
    }
}
