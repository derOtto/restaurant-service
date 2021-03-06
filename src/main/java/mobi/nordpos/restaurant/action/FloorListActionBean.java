/**
 * Copyright (c) 2012-2014 Nord Trading Network.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package mobi.nordpos.restaurant.action;

import java.sql.SQLException;
import java.util.List;
import mobi.nordpos.dao.model.Floor;
import mobi.nordpos.dao.model.Place;
import mobi.nordpos.dao.model.SharedTicket;
import mobi.nordpos.dao.factory.FloorPersist;
import mobi.nordpos.dao.factory.SharedTicketPersist;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.SimpleError;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

/**
 * @author Andrey Svininykh <svininykh@gmail.com>
 */
public class FloorListActionBean extends FloorBaseActionBean {

    private static final String FLOOR_LIST = "/WEB-INF/jsp/floor_list.jsp";

    private List<Floor> floorList;

    @DefaultHandler
    public Resolution view() {
        return new ForwardResolution(FLOOR_LIST);
    }

    public List<Floor> getFloorList() {
        return floorList;
    }

    public void setFloorList(List<Floor> floorList) {
        this.floorList = floorList;
    }

    @ValidationMethod
    public void validateFloorListIsAvalaible(ValidationErrors errors) {
        FloorPersist floorPersist = new FloorPersist();
        SharedTicketPersist sharedTicketPersist = new SharedTicketPersist();

        try {
            floorPersist.init(getDataBaseConnection());
            sharedTicketPersist.init(getDataBaseConnection());
            List<Floor> floors = floorPersist.readList();
            for (int i = 0; i < floors.size(); i++) {
                Floor floor = floors.get(i);
                List<Place> places = floorPersist.readPlaceList(floor);
                for (int j = 0; j < places.size(); j++) {
                    Place place = places.get(j);
                    SharedTicket ticket = sharedTicketPersist.read(place.getId());
                    place.setTicket(ticket);
                    places.set(j, place);
                }
                floor.setPlaceList(places);
                floors.set(i, floor);
            }
            setFloorList(floors);
        } catch (SQLException ex) {
            getContext().getValidationErrors().addGlobalError(
                    new SimpleError(ex.getMessage()));
        }
    }
}
