package net.turnbig.pandora.springboot.security.rpc;

import java.io.Serializable;
import java.util.Map;
import org.springframework.security.core.userdetails.UserDetails;

public interface RpcUserDetails extends Serializable, UserDetails {

  String getIdentify();

  String getUsername();

  Map<String, Object> profile();

}
