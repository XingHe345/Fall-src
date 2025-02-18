package dev.Fall.vialoadingbase.provider;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.protocols.base.BaseVersionProvider;
import dev.Fall.vialoadingbase.ViaLoadingBase;

public class VLBBaseVersionProvider extends BaseVersionProvider {
   public int getClosestServerProtocol(UserConnection connection) throws Exception {
      return connection.isClientSide() ? ViaLoadingBase.getInstance().getTargetVersion().getVersion() : super.getClosestServerProtocol(connection);
   }
}
