package fr.thedestiny.message.service;

import java.util.Arrays;
import java.util.List;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.thedestiny.Constants;
import fr.thedestiny.global.dto.GraphDto;
import fr.thedestiny.global.service.AbstractService;
import fr.thedestiny.message.dao.ContactDao;
import fr.thedestiny.message.dao.MessageDao;
import fr.thedestiny.message.dao.PhoneDao;
import fr.thedestiny.message.dto.MessageStatDto;

@Service
public class MessageStatService extends AbstractService {

	@Autowired
	private ContactDao contactDao;

	@Autowired
	private MessageDao messageDao;

	@Autowired
	private PhoneDao phoneDao;

	protected MessageStatService() {
		super(Constants.MESSAGES_CONTEXT);
	}

	public MessageStatDto computeStats() {
		MessageStatDto statDto = new MessageStatDto();
		statDto.setContactCount(contactDao.countContact());
		statDto.setPhoneCount(phoneDao.countPhone());
		statDto.setMessageCount(messageDao.countMessage());

		List<Object[]> messagesByDay = messageDao.countMessageByDay();

		buildGraphData(statDto.getGraphData(), messagesByDay);

		return statDto;
	}

	public GraphDto<List<Long>> computeStatsForContact(final int id) {
		List<Object[]> messagesByDay = messageDao.countMessageByDayForContact(id);

		return buildGraphData(new GraphDto<List<Long>>(), messagesByDay);
	}

	private GraphDto<List<Long>> buildGraphData(final GraphDto<List<Long>> graphData, final List<Object[]> messagesByDay) {
		graphData.setPointInterval(1000L * 60 * 60 * 24);
		graphData.setPointStart(String.valueOf(new LocalDate(messagesByDay.get(0)[0]).toDate().getTime()));

		LocalDate before = null;
		for (final Object[] current : messagesByDay) {
			LocalDate after = new LocalDate(current[0]);
			long value = ((Integer) current[1]).longValue();

			if (before != null) {
				Days days = Days.daysBetween(before, after);
				int gap = days.getDays();

				while (gap > 1) {
					before = before.plusDays(1);
					graphData.addStat(Arrays.asList(before.toDate().getTime(), 0L));
					gap--;
				}
			}

			graphData.addStat(Arrays.asList(after.toDate().getTime(), value));
			before = after;
		}

		return graphData;
	}
}
