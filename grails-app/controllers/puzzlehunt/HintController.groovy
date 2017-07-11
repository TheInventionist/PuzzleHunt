package puzzlehunt

import grails.converters.JSON
import grails.transaction.Transactional

class HintController {

    private static final EXTENSION_TYPES = [ //TODO DEDUPE
        "png": "image/png",
        "jpg": "image/jpg",
        "gif": "image/gif",
        "pdf": "application/pdf",
        "mp4": "video/mp4"
    ]

    def index() {

    }

    def getHints() {
        def pl = Player.findById(session.playerId)
        def ownedList = Hint.findByOwnerAndClosedNotEqual(pl,true)
        def ohl = []
        if (ownedList) {
            // TODO - is there a way to do this together with findAll (without for loop)
            for (def h in ownedList) {
                def oh = [
                    id: h.id,
                    question: h.question,
                    puzzle: h.puzzle.name,
                    player: h.player.name,
                    owner: h.owner ? h.owner.name : "--",
                    lastOwner: "--",
                    status: h.owner ? "unclaim" : "claim",
                    action : h.closed ? "closed" : "open",
                    createTime : h.createTime,
                    open : h.closed ? false : true,
                    orphan : h.owner ? false : true
                ]
                ohl.push(oh)
            }
        }
        def hintList = Hint.findAll("from Hint as h order by h.owner desc, h.createTime desc")
        def hdl = []
        // TODO - is there a way to do this together with findAll (without for loop)
        for (def h in hintList) {
            def hd = [
                id: h.id,
                question: h.question,
                puzzle: h.puzzle.name,
                player: h.player.name,
                owner: h.owner ? h.owner.name : "--",
                lastOwner: "--",
                status: h.owner ? "unclaim" : "claim",
                action : h.closed ? "closed" : "open",
                createTime : h.createTime,
                open : h.closed ? false : true,
                orphan : h.owner ? false : true
            ]
            hdl.push(hd)
        }

        def ret = [hints : hdl, owned : ohl]
        render ret as JSON
    }

    def refreshlist() {
        redirect controller: "hint", action: "index"
    }

    @Transactional
    def claimHint() {
        def user = Player.findById(session.playerId)
        def hint = Hint.findById(params.hintId)

        if (hint.owner && hint.owner == user) {
            render status: 500, text: "Already Owned"
            return
        }
        if (hint.owner && !params.steal) {
            render status: 500, text: "Someone else owns this hint"
            return
        }

        def ownedHints = Hint.findAllByOwnerAndClosedNotEqual(user, true)
        if (ownedHints.size()) {
            render status: 500, text: "Already own another hint"
            return
        }

        hint.owner = user

        if (!hint.save(flush: true)) {
            render status: 500, text: "Unknown Error"
            return
        }

        def ret = [success: true]
        render ret as JSON
    }

    @Transactional
    def unclaimHint() {
        def user = Player.findById(session.playerId)
        def hint = Hint.findById(params.hintId)

        if (hint.owner && hint.owner == user) {
            hint.owner = null
            if(! hint.save(flush : true)) {
                render status: 500
                return
            }
        } else {
            render status: 500
            return
        }

        def ret = [ success: true]
        render ret as JSON
    }


    def details() {
        println params
        if (!params.hintId) {
            render status: 500
            return
        }

    }

    def getHintDetails() {
        def hint = Hint.findById(params.hintId)
        def user = Player.findById(session.playerId)
        def ownedHints = Hint.findAllByOwnerAndClosedNotEqual(user, true)

        def hintData = [
            myName: user.name,
            myHints: ownedHints.size(),
            hintId: params.hintId,
            hinterName: hint.owner?.name,
            closed: hint.closed,
            playerName: hint.player.name,
            contactInfo: hint.contactInfo,
            puzzleName: hint.puzzle.name,
            question: hint.question,
            solutionAccessor: hint.puzzle?.solutionResource?.accessor,
            solution: hint.puzzle.solution,
            notes: hint.notes
        ]
        render hintData as JSON
    }


    @Transactional
    def updateNote() {
        def hint = Hint.findById(params.hintId)
        hint.notes = params.notes
        hint.save(flush : true)

        def ret = [success: true]
        render ret as JSON
    }

    @Transactional
    def closeHint() {
        def user = Player.findById(session.playerId)
        def hint = Hint.findById(params.hintId)

        if (hint.owner != user) {
            render status: 500, text: "You dont own this puzzle"
            return
        }

        if (hint.closed)  {
            render status: 500, text: "Already Closed"
            return
        }

        hint.closed = true
        hint.save(flush: true)

        def ret = [success: true]
        render ret as JSON
    }

    @Transactional
    def reopenHint() {
        def user = Player.findById(session.playerId)
        def hint = Hint.findById(params.hintId)

        if (!hint.closed)  {
            render status: 500, text: "Not Closed"
            return
        }

        hint.closed = false
        hint.save(flush: true)

        def ret = [success: true]
        render ret as JSON
    }

    def getResource() {
        def bootstrapPath = grailsApplication.config.getProperty("puzzlehunt.resourcePath")
        def rs = Resource.findByAccessor(params.accessor)

        if (rs) {
            def f = new File("${bootstrapPath}/${rs.filename}")
            def extension = rs.filename.substring(rs.filename.lastIndexOf(".") + 1).toLowerCase()

            render file:f, contentType: EXTENSION_TYPES[extension]
        } else {
            render status: 404
        }
    }
}
