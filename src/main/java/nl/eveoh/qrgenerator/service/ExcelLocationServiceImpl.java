package nl.eveoh.qrgenerator.service;

import nl.eveoh.qrgenerator.model.Location;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Erik van Paassen
 */
@Component
public class ExcelLocationServiceImpl implements LocationService {

    @Value("${Excel.HostKeyColumnHeader}")
    private String hostKeyColumnHeader;
	@Value("${Excel.LocationNameHeader}")
	private String locationNameHeader;


    @Override
    public ArrayList<Location> getLocations(String inputFilePath) {
        ArrayList<Location> locations = new ArrayList<>();

        Workbook wb;
        try {
            wb = WorkbookFactory.create(new File(inputFilePath));
        } catch (InvalidFormatException | IOException e) {
            throw new RuntimeException(e);
        }

        Sheet sheet = wb.getSheetAt(0);
        int rowIndex = 0;
        int hostKeyColumn = 0;
		int locationNameColumn = 0;
        for (Row row : sheet) {
            if (rowIndex == 0) {
                hostKeyColumn = getHostKeyCell(row);
				locationNameColumn = getLocationCell(row);
            } else {
                Location location = parseRow(row, hostKeyColumn, locationNameColumn);

                if (location != null) {
                    locations.add(location);
                }
            }

            rowIndex++;
        }

        return locations;
    }

    private int getHostKeyCell(Row row) {
        int cellIndex = 0;
        for (Cell cell : row) {
            if (hostKeyColumnHeader.equalsIgnoreCase(cell.getStringCellValue())) {
                return cellIndex;
            }

            cellIndex++;
        }

        return -1;
    }
    private int getLocationCell(Row row) {
        int cellIndex = 0;
        for (Cell cell : row) {
            if (locationNameHeader.equalsIgnoreCase(cell.getStringCellValue())) {
                return cellIndex;
            }

            cellIndex++;
        }

        return -1;
    }


    private Location parseRow(Row row, int hostKeyColumn, int locationNameColumn) {
        Location location = new Location();

        Cell hostKeyCell = row.getCell(hostKeyColumn);
		Cell locationNameCell = row.getCell(locationNameColumn);
        if (hostKeyCell == null||locationNameCell==null) {
            return null;
        }

        location.setHostKey(hostKeyCell.getStringCellValue());
		location.setLocationName(locationNameCell.getStringCellValue());

        return location;
    }
}