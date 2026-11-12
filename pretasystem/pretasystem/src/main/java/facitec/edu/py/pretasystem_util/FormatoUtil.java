package facitec.edu.py.pretasystem_util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/** Formato con separador de miles estilo Paraguay*/
public class FormatoUtil {

	private static DecimalFormatSymbols simbolos() {
		DecimalFormatSymbols s = new DecimalFormatSymbols();
		s.setGroupingSeparator('.');
		s.setDecimalSeparator(',');
		return s;
	}

	// Numero -> texto con puntos de miles (ej: 1500000 -> "1.500.000")
	public static String miles(Number n) {
		return new DecimalFormat("#,##0", simbolos()).format(n);
	}

	// Hace que un JSpinner muestre y acepte puntos de miles
	public static void aplicarMiles(JSpinner spn) {
		JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spn, "#,##0");
		editor.getFormat().setDecimalFormatSymbols(simbolos());
		spn.setEditor(editor);
		editor.getTextField().setHorizontalAlignment(JTextField.RIGHT);
		editor.getTextField().setValue(spn.getValue()); // vuelve a dibujar con el nuevo formato
	}

	// Renderer para columnas numericas de un JTable (alineadas a la derecha con puntos de miles)
	public static DefaultTableCellRenderer rendererMiles() {
		return new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;

			{
				setHorizontalAlignment(SwingConstants.RIGHT);
			}

			@Override
			protected void setValue(Object value) {
				setText(value instanceof Number ? miles((Number) value) : String.valueOf(value));
			}
		};
	}
}