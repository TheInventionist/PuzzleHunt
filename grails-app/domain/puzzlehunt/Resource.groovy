package puzzlehunt

class Resource {
    Puzzle puzzle
    Round round
    String filename
    String accessor
    boolean mustSolve
    String role

    static constraints = {
        puzzle nullable: true
        round nullable: true
        role nullable: true
    }
}
