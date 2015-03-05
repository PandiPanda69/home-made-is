package fr.thedestiny.auth.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationServiceTest {

	private AuthenticationService service = new AuthenticationService();

	private String encodedPwd = "0b9c2625dc21ef05f6ad4ddf47c5f203837aa32c";

	@Test
	public void testEncodePassword() {

		String encoded = service.encodePassword("toto");
		assertEquals(encodedPwd, encoded);
	}
}
