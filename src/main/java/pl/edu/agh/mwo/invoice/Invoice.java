package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import pl.edu.agh.mwo.invoice.product.Product;

public class Invoice {
	private Map<Product, Integer> products = new LinkedHashMap<Product, Integer>();

	private static Integer InvoiceAmount = 1;
	private Integer number;
	
	public Invoice () {
		number = InvoiceAmount++;
	}
	
	public void addProduct(Product product) {
		addProduct(product, 1);
	}

	public void addProduct(Product product, Integer quantity) {
		Integer tmpQuantity; 
		
		if (product == null || quantity <= 0) {
			throw new IllegalArgumentException();
		}
		if( products.containsKey(product) ) {
			tmpQuantity = products.get(product);
			products.remove(product);
			quantity += tmpQuantity;			
		}
		products.put(product, quantity);
	}

	public BigDecimal getNetTotal() {
		BigDecimal totalNet = BigDecimal.ZERO;
		for (Product product : products.keySet()) {
			BigDecimal quantity = new BigDecimal(products.get(product));
			totalNet = totalNet.add(product.getPrice().multiply(quantity));
		}
		return totalNet;
	}

	public BigDecimal getTaxTotal() {
		return getGrossTotal().subtract(getNetTotal());
	}

	public BigDecimal getGrossTotal() {
		BigDecimal totalGross = BigDecimal.ZERO;
		for (Product product : products.keySet()) {
			BigDecimal quantity = new BigDecimal(products.get(product));
			totalGross = totalGross.add(product.getPriceWithTax().multiply(quantity));
		}
		return totalGross;
	}
	
	public Integer getInvoiceNumber() {
		return number;
	}

	public String getAsText() {
		StringBuilder sb = new StringBuilder("");
		sb.append("Faktura nr " + this.number);
	
		Integer totalQuantity = new Integer(0);
		
		String ProductsDescription = "";
		DecimalFormat df = new DecimalFormat("0.00");
		
		BigDecimal PriceTotal = BigDecimal.ZERO;
		
		for (Product singleProduct: products.keySet()) {
			PriceTotal = singleProduct.getPrice().multiply(new BigDecimal(products.get(singleProduct)));
			
			sb.append("\n");
			sb.append(singleProduct.getName() );
			sb.append(" ");
			sb.append(products.get(singleProduct));
			sb.append(" ");
			sb.append(df.format(PriceTotal));
			
			totalQuantity += products.get(singleProduct);
		}
		
		sb.append("\nLiczba pozycji: ");
		sb.append(totalQuantity);
		
//		ProductsDescription = sb.toString();
//		System.out.println(ProductsDescription);
		return sb.toString();
	}


	
}
