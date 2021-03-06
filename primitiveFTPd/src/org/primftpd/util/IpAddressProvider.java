package org.primftpd.util;

import android.content.Context;
import android.widget.Toast;

import org.primftpd.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class IpAddressProvider {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public List<String> ipAddressTexts(Context ctxt, boolean verbose) {
        List<String> result = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
            while (ifaces.hasMoreElements()) {
                NetworkInterface iface = ifaces.nextElement();
                String ifaceDispName = iface.getDisplayName();
                String ifaceName = iface.getName();
                Enumeration<InetAddress> inetAddrs = iface.getInetAddresses();

                while (inetAddrs.hasMoreElements()) {
                    InetAddress inetAddr = inetAddrs.nextElement();
                    String hostAddr = inetAddr.getHostAddress();

                    logger.debug("addr: '{}', iface name: '{}', disp name: '{}', loopback: '{}'",
                            new Object[]{
                                    inetAddr,
                                    ifaceName,
                                    ifaceDispName,
                                    inetAddr.isLoopbackAddress()});

                    if (inetAddr.isLoopbackAddress()) {
                        continue;
                    }

                    if (hostAddr.contains("::")) {
                        // Don't include the raw encoded names. Just the raw IP addresses.
                        logger.debug("Skipping IPv6 address '{}'", hostAddr);
                        continue;
                    }

                    if (!verbose && !ifaceDispName.startsWith("wlan")) {
                        continue;
                    }

                    String displayText = hostAddr;
                    if (verbose) {
                        displayText += " (" + ifaceDispName + ")";
                    }

                    result.add(displayText);
                }
            }
        } catch (SocketException e) {
            logger.info("exception while iterating network interfaces", e);

            String msg = ctxt.getText(R.string.ifacesError) + e.getLocalizedMessage();
            Toast.makeText(ctxt, msg, Toast.LENGTH_SHORT).show();
        }
        return result;
    }
}
