/*
 * This file is part of ConfigHub.
 *
 * ConfigHub is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ConfigHub is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ConfigHub.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.confighub.api.user;

import com.confighub.api.server.auth.TokenState;
import com.confighub.core.error.ConfigException;
import com.confighub.core.store.Store;
import com.confighub.core.user.UserAccount;
import com.confighub.core.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.core.Response;

public abstract class AUserAccAccessValidation
{
    private static final Logger log = LogManager.getLogger(AUserAccAccessValidation.class);
    protected UserAccount user;

    protected int validate(String account,
                           String token,
                           Store store)
    {
        if (Utils.anyBlank(account))
        {
            // Bad request - 400
            return Response.Status.BAD_REQUEST.getStatusCode();
        }

        if (Utils.isBlank(token))
        {
            // Not authorized - 401
            return Response.Status.UNAUTHORIZED.getStatusCode();
        }

        try
        {

            this.user = TokenState.getUser(token, store);
            if (null == this.user)
            {
                // Not authorized - 401
                return Response.Status.UNAUTHORIZED.getStatusCode();
            }

        }
        catch (ConfigException e)
        {
            log.error(e.getMessage());
            e.printStackTrace();

            // Server error - 500
            return Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        }

        return 0;
    }
}