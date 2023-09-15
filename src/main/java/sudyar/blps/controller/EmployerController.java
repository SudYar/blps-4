package sudyar.blps.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import sudyar.blps.dto.request.ChoosenRequest;
import sudyar.blps.dto.request.FeedbackRequest;
import sudyar.blps.dto.request.OrderingRequest;
import sudyar.blps.dto.response.InfoResponse;
import sudyar.blps.entity.Feedback;
import sudyar.blps.entity.Notice;
import sudyar.blps.entity.Ordering;
import sudyar.blps.entity.Status;
import sudyar.blps.service.FeedbackService;
import sudyar.blps.service.NoticeService;
import sudyar.blps.service.OrderService;
import sudyar.blps.service.UserService;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/employer")
public class EmployerController {
    final private Integer DIFF_TIME = 1000000;


    Logger logger = LoggerFactory.getLogger(EmployerController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping("/infoOrder")
    public InfoResponse getInfo(){
        return new InfoResponse(OrderingRequest.getInfo(), 0);
    }

    @PostMapping("/newOrder")
    public InfoResponse newOrder(@RequestBody OrderingRequest order){
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.warn(login);
        orderService.createOrdering(order, login);
        return new InfoResponse("Success", 0);
    }

    @DeleteMapping("/deleteOrder")
    public InfoResponse deleteById(@RequestBody Integer idOrdering){
        Optional<Ordering> order = orderService.getById(idOrdering);
        if (order.isPresent()) {
            Ordering ordering = order.get();
            String login = SecurityContextHolder.getContext().getAuthentication().getName();
            if (login.equals(ordering.getOwnerLogin())){
                orderService.deleteOrdering(idOrdering);
                return new InfoResponse("Удален заказ: " + ordering.toString(), 0);
            }
            else return new InfoResponse("Вы не владелец данного заказа", 1);
        }
        else   return new InfoResponse("Нет заказа с таким id", 1);
    }

    @DeleteMapping("/deleteAllOrders")
    public InfoResponse deleteAllOrder(){
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        int count = orderService.deleteAllOrdering(login);
        return new InfoResponse("Удалено заказов: " + count, 0);

    }


    @GetMapping("/getNotice")
    public ResponseEntity<?> getNotice(){
        return ResponseEntity.ok(noticeService.getAll(SecurityContextHolder.getContext().getAuthentication().getName()));
    }
    @PostMapping("/chooseExecutor")
    public InfoResponse chooseExecutor (@RequestBody ChoosenRequest choosenRequest){
        int idOrder = choosenRequest.getIdOrder();
        String executor = choosenRequest.getExecutor();
        Optional<Ordering> optionalOrdering = orderService.getById(idOrder);
        logger.warn(executor);
        if (optionalOrdering.isPresent()){
            Ordering order = optionalOrdering.get();
            String employer = SecurityContextHolder.getContext().getAuthentication().getName();
            if (noticeService.existChosenOrdering(employer, executor, order, Status.READY) ){
                noticeService.deleteNotices(order, Status.READY);
                noticeService.deleteNotices(order, Status.READY2);
                noticeService.deleteNotices(order, Status.READY3);
                noticeService.deleteNotices(order, Status.START2);
                noticeService.deleteNotices(order, Status.START3);
                noticeService.createNotice(executor, employer, "Можете приступать к работе", order, Status.IN_WORK);
                return new InfoResponse("Ответ исполнителю отправлен", 0);
            } else return new InfoResponse("Исполнитель не брался за этот заказ", 1);

        }else return new InfoResponse("Нет заказа с таким id", 1);
    }

    @PostMapping("/startNewRound")
    public InfoResponse startNewRound(@RequestBody int idOrder){
        Optional<Ordering> optionalOrdering = orderService.getById(idOrder);
        if (optionalOrdering.isPresent()) {
            Ordering order = optionalOrdering.get();
            String employer = SecurityContextHolder.getContext().getAuthentication().getName();
            if (noticeService.existByStatusAndOrdering(order, Status.START3)) {
                return new InfoResponse("Вы не можете пойти на 4 круг. Необходимо выбрать исполнителя", 1);
            } else if (noticeService.existByStatusAndOrdering(order, Status.START2)) {
                List<Notice> notices = noticeService.getByStatusAndOrdering(order, Status.READY2);
                for (Iterator<Notice> iter = notices.iterator(); iter.hasNext(); ) {
                    Notice element = iter.next();
                    Date ldt = new Date(element.getCreatedDate().getTime() + DIFF_TIME);
                    Date ldtNow = new Date();
                    if (ldt.after(ldtNow)) iter.remove();
                }
                if (notices.size() < 2)
                    return new InfoResponse("Не хватает ответивших вовремя исполнителей (должно быть как минимум два)\nОтвет: " + notices, 1);
                else {
                    for (Iterator<Notice> iter = notices.iterator(); iter.hasNext(); ) {
                        Notice element = iter.next();
                        noticeService.createNotice(element.getFromUser(), employer, "Появились новые условия, так как несколько исполниетелей", order, Status.START3);
                    }
                    return new InfoResponse("Начался следующий раунд\nПредыдущие ответы: " + notices, 0);
                }
            } else {
                List<Notice> notices = noticeService.getByStatusAndOrdering(order, Status.READY);
                for (Iterator<Notice> iter = notices.iterator(); iter.hasNext(); ) {
                    Notice element = iter.next();
                    Date ldt = new Date(element.getCreatedDate().getTime() + DIFF_TIME);
                    Date ldtNow = new Date();
                    if (ldt.after(ldtNow)) iter.remove();
                }
                if (notices.size() < 2)
                    return new InfoResponse("Не хватает ответивших вовремя исполнителей (должно быть как минимум два)\nОтвет: " + notices, 1);
                else {
                    for (Iterator<Notice> iter = notices.iterator(); iter.hasNext(); ) {
                        Notice element = iter.next();
                        noticeService.createNotice(element.getFromUser(), employer, "Появились новые условия, так как несколько исполниетелей", order, Status.START2);
                    }

                    return new InfoResponse("Начался следующий раунд\nПредыдущие ответы: " + notices, 0);
                }
            }
        }else return new InfoResponse("Нет заказа с таким id", 1);
    }

    @PostMapping("/finishOrder")
    public InfoResponse finishOrder(@RequestBody FeedbackRequest feedbackRequest){
        int starsForFeedback = feedbackRequest.getStarsForFeedback();
        int idOrder = feedbackRequest.getIdOrder();
        if (starsForFeedback<0 || starsForFeedback > 10 ) return new InfoResponse("Количество звезд должно быть целым в пределах [0;10]",1);
        Optional<Ordering> optionalOrdering = orderService.getById(idOrder);
        if (optionalOrdering.isPresent()){
            Ordering order = optionalOrdering.get();
            String employer = SecurityContextHolder.getContext().getAuthentication().getName();
            List<Notice> finishNotice =  noticeService.getByStatusAndOrdering(order, Status.FINISH);
            if (finishNotice.size() != 1) return new InfoResponse("Зазаз ещё не закончен", 1);
            else {
                String executor = finishNotice.get(0).getFromUser();
                Feedback feedback = new Feedback();
                feedback.setDescription(order.getDescription());
                feedback.setAddress(order.getAddress());
                feedback.setLoginEmployer(employer);
                feedback.setLoginExecutor(executor);
                feedback.setStars(starsForFeedback);
                feedbackService.add(feedback);
                noticeService.deleteNotices(order, Status.FINISH);
                noticeService.deleteNotices(order, Status.MESSAGE);
                orderService.deleteOrdering(idOrder);
                return new InfoResponse("Поздравляю с выполненым заказом, спасибо за вашу оценку: " + feedback, 0);
            }

        }else return new InfoResponse("Нет заказа с таким id", 1);
    }
}
