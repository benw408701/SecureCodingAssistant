package testProject;

import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.SecureClassLoader;

public class Test15SEC extends SecureClassLoader {

	// Test SEC07J
	@Override
	protected PermissionCollection getPermissions(CodeSource cs) {
		// Comment out this line for error
		super.getPermissions(cs);
		return null;
	}
}
