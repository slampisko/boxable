
/*
 Quodlibet.be
 */
package be.quodlibet.boxable;

import java.awt.Color;
import java.io.IOException;
import java.util.function.Function;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class Cell<T extends PDPage> {

	private float width;
	private String text;

	private PDFont font = PDType1Font.HELVETICA;
	private PDFont fontBold = PDType1Font.HELVETICA_BOLD;
	
	private float fontSize = 8;
	private Color fillColor;
	private Color textColor = Color.BLACK;
	private final Row<T> row;
	private Function<String, String[]> wrappingFunction;
	private boolean isHeaderCell = false;

	// default padding
	private float leftPadding = 5f;
	private float rightPadding = 5f;
	private float topPadding = 5f;
	private float bottomPadding = 5f;

	private static final Function<String, String[]> DEFAULT_WRAP_FUNC
		= t -> t.split("(?<=\\s|-|@|,|\\.|:|;)");

	private final HorizontalAlignment align;
	private final VerticalAlignment valign;

	float horizontalFreeSpace = 0;
	float verticalFreeSpace = 0;

	/**
	 * 
	 * @param width
	 *            in % of table width
	 * @param text
	 */
	Cell(Row<T> row, float width, String text, boolean isCalculated, HorizontalAlignment align, VerticalAlignment valign, Function<String, String[]> wrappingFunc) {
		this.row = row;
		if (isCalculated) {
			double calclulatedWidth = ((row.getWidth() * width) / 100);
			this.width = (float) calclulatedWidth;
		} else {
			this.width = width;
		}

		if (getWidth() > row.getWidth()) {
			throw new IllegalArgumentException(
					"Cell Width=" + getWidth() + " can't be bigger than row width=" + row.getWidth());
		}
		this.text = text == null ? "" : text;
		this.align = align;
		this.valign = valign;
		this.wrappingFunction = wrappingFunc;
	}

	/**
	 * 
	 * @param width
	 *            in % of table width
	 * @param text
	 */
	Cell(Row<T> row, float width, String text, boolean isCalculated, HorizontalAlignment align, VerticalAlignment valign) {
		this(row, width, text, isCalculated, align, valign, null);
	}

	public Color getTextColor() {
		return textColor;
	}

	public void setTextColor(Color textColor) {
		this.textColor = textColor;
	}

	public Color getFillColor() {
		return fillColor;
	}

	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}

	public float getWidth() {
		return width;
	}

	public float getInnerWidth() {
		return getWidth() - getLeftPadding() - getRightPadding();
	}

	public float getInnerHeight() {
		return getHeight() - getBottomPadding() - getTopPadding();
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public PDFont getFont() {
		if (font == null) {
			throw new IllegalArgumentException("Font not set.");
		}
		return font;
	}

	public void setFont(PDFont font) {
		this.font = font;
	}

	public float getFontSize() {
		return fontSize;
	}

	public void setFontSize(float fontSize) {
		this.fontSize = fontSize;
	}

	public Paragraph getParagraph() {
		return new Paragraph(text, font, fontSize, getInnerWidth(), align, getWrappingFunction());
	}

	public float getExtraWidth() {
		return this.row.getLastCellExtraWidth() + getWidth();
	}

	public float getHeight() {
		return row.getHeight();
	}

	public float getTextHeight() {
		return getParagraph().getHeight();
	}

	public float getLeftPadding() {
		return leftPadding;
	}

	public void setLeftPadding(float cellLeftPadding) {
		this.leftPadding = cellLeftPadding;
	}

	public float getRightPadding() {
		return rightPadding;
	}

	public void setRightPadding(float cellRightPadding) {
		this.rightPadding = cellRightPadding;
	}

	public float getTopPadding() {
		return topPadding;
	}

	public void setTopPadding(float cellTopPadding) {
		this.topPadding = cellTopPadding;
	}

	public float getBottomPadding() {
		return bottomPadding;
	}

	public void setBottomPadding(float cellBottomPadding) {
		this.bottomPadding = cellBottomPadding;
	}

	public float getVerticalFreeSpace() {
		return getInnerHeight() - getTextHeight();
	}

	public float getHorizontalFreeSpace() {
		float tw = 0.0f;
		try {
			for (final String line : getParagraph().getLines()) {
				tw = Math.max(tw, getFont().getStringWidth(line.trim()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		tw = tw / 1000 * getFontSize();
		return getInnerWidth() - tw;
	}

	public PDFont getFontBold() {
		return fontBold;
	}
	
	public void setFontBold(PDFont fontBold) {
		this.fontBold = fontBold;
	}

	public HorizontalAlignment getAlign() {
		return align;
	}

	public VerticalAlignment getValign() {
		return valign;
	}

	public boolean isHeaderCell() {
		return isHeaderCell;
	}

	public void setHeaderCell(boolean isHeaderCell) {
		this.isHeaderCell = isHeaderCell;
	}

	public Function<String, String[]> getWrappingFunction() {
		if (null == wrappingFunction) {
			wrappingFunction = DEFAULT_WRAP_FUNC;
		}

		return wrappingFunction;
	}

	public void setWrappingFunction(Function<String, String[]> wrappingFunction) {
		this.wrappingFunction = wrappingFunction;
	}
}
