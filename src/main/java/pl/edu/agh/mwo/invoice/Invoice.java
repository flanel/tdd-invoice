package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import pl.edu.agh.mwo.invoice.product.Product;

public class Invoice {
	private Collection<Product> products = new ArrayList<Product>();

	public void addProduct(Product product) {
		products.add(product);
		System.out.println("String testowy cena = " + product.getPriceWithTax());
	}

	public void addProduct(Product product, Integer quantity) {
		if(quantity <= 0) {
			throw new IllegalArgumentException("Wrong quantity of product, = " + quantity);
		}
		
		while( quantity > 0 ) {
			quantity--;
			products.add(product);
		}
		return; 
	}
	

	public BigDecimal getSubtotal() {
		BigDecimal subtotal = new BigDecimal(0);

		for(Product product : products) {
			subtotal = subtotal.add(product.getPrice());
		}
		return subtotal;
	}

	public BigDecimal getTax() {
		BigDecimal tax = new BigDecimal(0);

		for(Product product : products) {
			tax = tax.add( product.getPrice().multiply(product.getTaxPercent()) );
//			System.out.println( product.getPrice().multiply(product.getTaxPercent()) );
		}	
		
		return tax;
	}

	public BigDecimal getTotal() {
		BigDecimal total = new BigDecimal(0);

		for(Product product : products) {
			total = total.add( product.getPriceWithTax() );
		}	
		
		return total;
	}
}
