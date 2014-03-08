package org.eiichiro.bootleg;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.eiichiro.bootleg.URITemplate;
import org.junit.Test;

public class URITemplateTest {

	@Test
	public void testURITemplate() {
		new URITemplate("path/to/endpoint/method");
		new URITemplate("path/to/endpoint/{m-e_t.h--od}");
		new URITemplate("path/to/{endpoint}/{method}");
		new URITemplate("path/to/{endpoint}/*");
		new URITemplate("path/to/*/*");
		new URITemplate("*/endpoint/method");
		new URITemplate("*");
		
		try {
			new URITemplate(null);
			fail();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		try {
			// c == '}'
			new URITemplate("{path/to/endpoint/method");
			fail();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		try {
			// substring.contains("{")
			new URITemplate("{path{/to/endpoint/method");
			fail();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		try {
			// substring.contains("}")
			new URITemplate("}path/to/endpoint/method");
			fail();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		try {
			// c == '}'
			new URITemplate("path/to/endpoint/method{");
			fail();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		try {
			// substring.contains("}")
			new URITemplate("path/to/endpoint/{method}}");
			fail();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		try {
			// substring.contains("{")
			new URITemplate("path/to/{{endpoint}/method");
			fail();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		try {
			// substring.contains("}")
			new URITemplate("path/to/endpoint}/{method");
			fail();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		try {
			// substring.length() == 0
			new URITemplate("path/to/endpoint/{}");
			fail();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		try {
			// !substring.matches("^\\p{Alnum}[\\p{Alnum}|.|_|-]*")
			new URITemplate("path/to/endpoint/{.method}");
			fail();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		try {
			// !substring.matches("^\\p{Alnum}[\\p{Alnum}|.|_|-]*")
			new URITemplate("path/to/endpoint/{_method}");
			fail();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		try {
			// !substring.matches("^\\p{Alnum}[\\p{Alnum}|.|_|-]*")
			new URITemplate("path/to/endpoint/{-method}");
			fail();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		try {
			// !substring.matches("^\\p{Alnum}[\\p{Alnum}|.|_|-]*")
			new URITemplate("path/to/endpoint/{?method}");
			fail();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		try {
			// template.contains("}{")
			new URITemplate("path/to/{endpoint}/{endpoint}");
			fail();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		try {
			// variables.contains(substring)
			new URITemplate("path/to/{end}{point}/method");
			fail();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testMatches() {
		assertTrue(new URITemplate("path/to/endpoint/method").matches("path/to/endpoint/method"));
		assertTrue(new URITemplate("path/to/endpoint/{m-e_t.h--od}").matches("path/to/endpoint/method"));
		assertTrue(new URITemplate("path/to/{endpoint}/{method}").matches("path/to/endpoint/method"));
		assertTrue(new URITemplate("path/to/{endpoint}/*").matches("path/to/endpoint/method"));
		assertTrue(new URITemplate("path/to/*/*").matches("path/to/endpoint/method"));
		assertTrue(new URITemplate("*/endpoint/method").matches("path/to/endpoint/method"));
		assertTrue(new URITemplate("*").matches("path/to/endpoint/method"));
		assertTrue(new URITemplate("path/to/{end}.{point}/{method}").matches("path/to/end.poi.nt/method"));
		assertFalse(new URITemplate("path/to/endpoint/method").matches("path/to/endpoint/metho"));
		assertFalse(new URITemplate("path/to/endpoint/method").matches("path/to/endpoint/methodd"));
		assertFalse(new URITemplate("path/to/{end}.{point}/{method}").matches("path/to/endpoint/method"));
	}

	@Test
	public void testVariables() {
		assertTrue(new URITemplate("path/to/endpoint/method").variables().isEmpty());
		List<String> variables = new URITemplate("path/to/{endpoint}/{method}").variables();
		assertThat(variables.size(), is(2));
		assertThat(variables.get(0), is("endpoint"));
		assertThat(variables.get(1), is("method"));
		variables = new URITemplate("path/to/{endpoint}/*").variables();
		assertThat(variables.size(), is(1));
		assertThat(variables.get(0), is("endpoint"));
		variables = new URITemplate("path/to/{end}.{point}/{method}").variables();
		assertThat(variables.size(), is(3));
		assertThat(variables.get(0), is("end"));
		assertThat(variables.get(1), is("point"));
		assertThat(variables.get(2), is("method"));
	}

	@Test
	public void testVariablesString() {
		Map<String, String> variables = new URITemplate("path/to/endpoint/method").variables("path/to/endpoint/method");
		assertTrue(variables.isEmpty());
		variables = new URITemplate("path/to/endpoint/{m-e_t.h--od}").variables("path/to/endpoint/method");
		assertThat(variables.size(), is(1));
		assertThat(variables.get("m-e_t.h--od"), is("method"));
		variables = new URITemplate("path/to/{endpoint}/{method}").variables("path/to/endpoint/method");
		assertThat(variables.size(), is(2));
		assertThat(variables.get("endpoint"), is("endpoint"));
		assertThat(variables.get("method"), is("method"));
		variables = new URITemplate("path/to/{endpoint}/*").variables("path/to/endpoint/method");
		assertThat(variables.size(), is(1));
		assertThat(variables.get("endpoint"), is("endpoint"));
		variables = new URITemplate("path/to/*/*").variables("path/to/endpoint/method");
		assertTrue(variables.isEmpty());
		variables = new URITemplate("path/to/{end}.{point}/{method}").variables("path/to/end.point/method");
		assertThat(variables.size(), is(3));
		assertThat(variables.get("end"), is("end"));
		assertThat(variables.get("point"), is("point"));
		assertThat(variables.get("method"), is("method"));
		variables = new URITemplate("path/to/{end}.{point}/{method}").variables("path/to/end.poi.nt/method");
		assertThat(variables.size(), is(3));
		assertThat(variables.get("end"), is("end"));
		assertThat(variables.get("point"), is("poi.nt"));
		assertThat(variables.get("method"), is("method"));
		variables = new URITemplate("path/to/endpoint/method").variables("path/to/endpoint/metho");
		assertTrue(variables.isEmpty());
		variables = new URITemplate("path/to/endpoint/method").variables("path/to/endpoint/methodd");
		assertTrue(variables.isEmpty());
		variables = new URITemplate("path/to/{end}.{point}/{method}").variables("path/to/endpoint/method");
		assertTrue(variables.isEmpty());
	}

	@Test
	public void testCompareTo() {
		assertTrue(new URITemplate("path/to/endpoint/method").compareTo(new URITemplate("path/to/endpoint/method")) == 0);
		assertTrue(new URITemplate("path/to/endpoint/method").compareTo(new URITemplate("path/to/endpoint")) < 0);
		assertTrue(new URITemplate("path/to/endpoint/method").compareTo(new URITemplate("path/to/end/point/method")) > 0);
		assertTrue(new URITemplate("path/to/endpoint/method").compareTo(new URITemplate("path/to/endpoint/")) < 0);
		assertTrue(new URITemplate("path/to/endpoint/method").compareTo(new URITemplate("path/to/endpoint/*")) > 0);
		assertTrue(new URITemplate("path/to/endpoint/method").compareTo(new URITemplate("path/to/endpoint/{method}")) > 0);
	}

}
